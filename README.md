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
* Ability to receive webhooks (with something like [ngrok](https://developers.telnyx.com/docs/v2/development/ngrok?utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link))
* [Java](https://developers.telnyx.com/docs/v2/development/dev-env-setup?lang=java&utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link) installed

## Demos

| Demo                                                                             | Description                                                                                       |
|:---------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------------------|
| [sparkjava-demo-enterprise-integration](./sparkjava-demo-enterprise-integration) | A spark-java server that proxies requests to/from Telnyx with some higher level application logic |
| [spring-messaging-auto-response](./spring-messaging-auto-response)               | Using Spring to auto-respond to various inbound messages                                          |
| [spring-phonenumber-search-and-order](./spring-phonenumber-search-and-order)     | Search and order phone numbers via an API                                                         |
| [spring-masked-calling](./spring-masked-calling)                                 | Proxy/Mask calls and texts from user-A to user-B through Telnyx Call Control and Messaging        |

## Usage

The following environmental variables need to be set for most demos in this repo

| Variable            | Description                                                                                                                                              |
|:--------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------|
| `TELNYX_API_KEY`    | Your [Telnyx API Key](https://portal.telnyx.com/#/app/api-keys?utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link)              |
| `TELNYX_PUBLIC_KEY` | Your [Telnyx Public Key](https://portal.telnyx.com/#/app/account/public-key?utm_source=referral&utm_medium=github_referral&utm_campaign=cross-site-link) |
| `TELNYX_APP_PORT`   | **Defaults to `8000`** The port the app will be served                                                                                                   |

### .env file

This app uses the excellent [dotenv-java](https://github.com/cdimascio/dotenv-java) package to manage environment variables.

Make a copy of [`.env.sample`](./.env.sample) and save as `.env` and update the variables to match your creds.

```
TELNYX_API_KEY=
TELNYX_PUBLIC_KEY=
TENYX_APP_PORT=8000
```
