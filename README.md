# Grails Web Profile

A profile for creating standard Grails web applications, with popular CSS framework **Bootstrap**.

## Grails Version

- Grails **5.0.0**
- Grails Base Profile **5.0.3**
- Grails Scaffolding Plugin **4.0.0**
- Grails Fields Plugin **3.0.0.RC1**

## Usage

### Build

```
git clone web-bootstrap
cd web-bootstrap
./gradlew publishToMavenLocal
```

### Create App

```
grails create-app --profile org.grails.profiles:web-bootstrap:5.0.0-SNAPSHOT org.grails.demo.web-bootstrap-demo
```

## What's New

### 5.0.0

* Update Grails 5.0
* Update jQuery 3.5.1, Bootstrap 4.6.1
* Update Grails Scaffolding and Fields default templates
* Support Bootstrap form component, powerful grid system and responsive layout
* Support Bootstrap Icons
* Add Bootstrap taglib, support paginate and datePicker with more options
* Add messages_zh_CN.properties and messages_zh_TW.properties
* Default main layout support load javascript by conversation
* Add Languages menu
* Add Management menu

## Links

- [Grails](https://grails.org)
- [Grails Github](https://github.com/grails)
- [Grails Fiedls Plugin](https://grails-fields-plugin.github.io/grails-fields/)
- [Bootstrap](https://getbootstrap.com)
- [Bootswatch](https://bootswatch.com)
- [jQuery](https://jquery.com)
- [Popper](https://popper.js.org)