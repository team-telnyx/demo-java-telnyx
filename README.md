<div align="center">

# Telnyx Java Getting Started

![Telnyx](logo-dark.png)

Sample application demonstrating Java SDK Basics

</div>

## Documentation & Tutorial

The full documentation and tutorial is available on [developers.telnyx.com](https://developers.telnyx.com/docs/v2/call-control/tutorials/conferencing-demo?lang=php&utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link)

## Pre-Reqs

You will need to set up:

* [Telnyx Account](https://telnyx.com/sign-up?utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link)
* [Telnyx Phone Number](https://portal.telnyx.com/#/app/numbers/my-numbers?utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link) enabled with:
  * [Telnyx Call Control Application](https://portal.telnyx.com/#/app/call-control/applications?utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link)
  * [Telnyx Outbound Voice Profile](https://portal.telnyx.com/#/app/outbound-profiles?utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link)
* Ability to receive webhooks (with something like [ngrok](https://developers.telnyx.com/docs/v2/development/ngrok?utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link))
* [Java](https://developers.telnyx.com/docs/v2/development/dev-env-setup?lang=java&utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link) installed

## What you can do

* Send a text message and inspect the DLR response
* Send a search request to look for available Numbers

## Usage

The following environmental variables need to be set

| Variable            | Description                                                                                                                                              |
|:--------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------|
| `TELNYX_API_KEY`    | Your [Telnyx API Key](https://portal.telnyx.com/#/app/api-keys?utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link)              |
| `TELNYX_PUBLIC_KEY` | Your [Telnyx Public Key](https://portal.telnyx.com/#/app/account/public-key?utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link) |
| `TELNYX_APP_PORT`   | **Defaults to `8000`** The port the app will be served                                                                                                   |

### .env file

This app uses the excellent [java dotenv](https://github.com/cdimascio/java-dotenv) package to manage environment variables.

Make a copy of [`.env.sample`](./.env.sample) and save as `.env` and update the variables to match your creds.

```
TELNYX_API_KEY=
TELNYX_PUBLIC_KEY=
TENYX_APP_PORT=8000
```

### Callback URLs For Telnyx Applications

| Callback Type                    | URL                                        |
|:---------------------------------|:-------------------------------------------|
| Inbound Voice Callback           | `{ngrok-url}/Callbacks/Voice/Inbound`      |
| Inbound Message Callback         | `{ngrok-url}/Callbacks/Messaging/Inbound`  |
| Outbound Message Status Callback | `{ngrok-url}/Callbacks/Messaging/Outbound` |

### Install

Run the following commands to get started

```
$ git clone https://github.com/d-telnyx/demo-java-telnyx.git
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

### Run

Open your IDE and run the application

#### Sending Messages

Send a `POST` request to `http://{your-url}.ngrok.io/SendMessage` and the Server will proxy the request to your Telnyx account

| Parameter | Description                  | Example         |
|:----------|:-----------------------------|:----------------|
| `to`      | The destination phone number | `+19198675309`  |
| `from`    | The Telnyx phone number      | `+191976429067` |
| `text`    | The actual message content   | `hello world 👋` |


```http
POST http://your-url.ngrok.io/SendMessage HTTP/1.1
Content-Type: application/json; charset=utf-8

{
    "to": "+19198675309",
    "from": "+191976429067",
    "text": "hello world 👋"
}
```

#### Searching Phone numbers

Send a `GET` request to `http://{your-url}.ngrok.io/availableNumbers` and the Server will proxy the request to your Telnyx account

| Query Parameter | Description                       | Example   | Required |
|:----------------|:----------------------------------|:----------|:---------|
| `countryCode`   | The country searching             | `US`      | True     |
| `state`         | The Canadian Province or US State | `NC`      | True     |
| `city`          | The city                          | `Raleigh` | True     |


```http
GET http://your-url.ngrok.io/availableNumbers?countryCode=US&city=Raleigh&state=NC HTTP/1.1
```

## Next Steps