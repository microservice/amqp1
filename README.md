# _AMQP1_ OMG Microservice

[![Open Microservice Guide](https://img.shields.io/badge/OMG%20Enabled-👍-green.svg?)](https://microservice.guide)

An OMG service for AMQP are message orientation, queuing, routing (including point-to-point and publish-and-subscribe), reliability and security.

## Direct usage in [Storyscript](https://storyscript.io/):

##### Publish Text
```coffee
amqp1 publishText exchange:'exchange' content:'content' properties:'{"map":"properties"}
```

Curious to [learn more](https://docs.storyscript.io/)?

✨🍰✨

## Usage with [OMG CLI](https://www.npmjs.com/package/omg)

##### Publish Text
```shell
$ omg run publishText -a exchange=<EXCHANGE> -a content=<CONTENT> -a properties=<MAP_OF_PROPERTIES> -e AMQP_URL=<AMQP_URL>
```
##### Subscribe Text
```shell
$ omg subscribe subscribeText exchange -a name=<EXCHANGE_NAME> -e AMQP_URL=<AMQP_URL>
```

**Note**: The OMG CLI requires [Docker](https://docs.docker.com/install/) to be installed.

## License
[MIT License](https://github.com/omg-services/ampq1/blob/master/LICENSE).
