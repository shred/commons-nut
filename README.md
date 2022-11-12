# commons-nut ![build status](https://shredzone.org/badge/commons-nut.svg) ![maven central](https://shredzone.org/maven-central/org.shredzone.commons/commons-nut/badge.svg)

A simple Java NUT ([Network UPS Tools](https://networkupstools.org)) client.

This software is part of the Shredzone Commons.

**Experimental!** I wrote this client because I found no other one at Maven Central. It is a very simple implementation that does not cover all protocol features, and is also not quite well unit tested.

For me it is sufficient like that, so don't expect frequent updates. However, please give feedback if you're using this client in your project (for example by starring it at GitHub). If there is sufficient interest in this client, it would motivate me to invest more time into its development.

## Features

* Connects to many UPS via a NUT server.
* Supports the most important commands of the protocol.
* Small footprint, only requires Java 11 or higher.
* Logging via SLF4J
* [Semantic Versioning](https://semver.org).

## Quick Start

Just create a `Client` object as starting point, and use its methods.

```java
try (Client client = new Client("localhost")) {
    client.getDeviceList().forEach(System.err::println);

    Device ups = client.getDevice("myups");

    ups.getVariables().forEach(System.err::println);
    ups.getRWVariables().forEach(System.err::println);
    ups.getCommands().forEach(System.err::println);
} catch (NutException ex) {
    ex.printStackTrace();
}
```

## Limitations

This client implements only the most important features of the [NUT protocol](https://networkupstools.org/docs/developer-guide.chunked/ar01s09.html) 1.2 or higher.

These commands are not supported yet:

* `GET TYPE`, `LIST ENUM`, `LIST RANGE`
* `GET TRACKING`, `SET TRACKING`
* `LIST CLIENT`
* `PRIMARY`, `FSD`
* `STARTTLS` and socket encryption

## Debugging

By setting the log level of the `org.shredzone.commons.nut` package to `DEBUG`, the communication with the NUT server will be logged.

**Caution:** The `DEBUG` log output contains the login credentials! Do not use it on production level.

## Contribute

* Fork the [Source code at GitHub](https://github.com/shred/commons-nut). Feel free to send pull requests.
* Found a bug? [File a bug report!](https://github.com/shred/commons-nut/issues)

## License

_commons-nut_ is open source software. The source code is distributed under the terms of [GNU Lesser General Public License Version 3](http://www.gnu.org/licenses/lgpl-3.0.html).
