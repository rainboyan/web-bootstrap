# Grails Web Profile

A profile for creating standard Grails web applications, with popular CSS framework **Bootstrap**.

## Grails Version

- Grails **5.0.0**
- Grails Base Profile **5.0.6**
- Grails Scaffolding Plugin **4.0.0**
- Grails Fields Plugin **3.0.0.RC1**

## Usage

### Build Profile

```
git clone https://github.com/rainboyan/web-bootstrap.git
cd web-bootstrap
./gradlew publishToMavenLocal
```

### Create App

#### Use Bootstrap CSS

```
grails create-app --profile org.grails.profiles:web-bootstrap:5.0.0-SNAPSHOT --features bootstrap org.grails.demo.web-bootstrap-demo
cd web-bootstrap-demo
grails run-app
```
#### Use Bootstrap with SASS and NPM

```
grails create-app --profile org.grails.profiles:web-bootstrap:5.0.0-SNAPSHOT --features bootstrap-sass org.grails.demo.web-bootstrap-sass-demo
cd web-bootstrap-sass-demo
npm install
npm run build
grails run-app
```

## What's New

### 4.0.0

* Update Grails 5.0
* Update jQuery 3.6.0, Bootstrap 5.1.3
* Update Grails Scaffolding and Fields default templates
* Support Bootstrap form component, powerful grid system and responsive layout
* Support Bootstrap Icons v1.8
* Support Bootstrap with SASS and NPM
* Support Bootswatch themes
* Add Bootstrap taglib, support paginate and datePicker with more options
* Add messages_zh_CN.properties and messages_zh_TW.properties
* Default main layout support load javascript by convention
* Add Languages menu
* Add Management menu
* Add Themes menu
* Remove unsed css in main.css and grails.css
* Remove unsed skin images

## Links

- [Grails](https://grails.org)
- [Grails Github](https://github.com/grails)
- [Grails Fiedls Plugin](https://grails-fields-plugin.github.io/grails-fields/)
- [Grails Web Bootstrap Profile](https://github.com/rainboyan/web-bootstrap)
- [Grails Web Bootstrap Demo](https://github.com/rainboyan/scaffold-bootstrap-layout-demo)
- [Bootstrap](https://getbootstrap.com)
- [Bootstrap npm starter](https://github.com/twbs/bootstrap-npm-starter)
- [Bootswatch](https://bootswatch.com)
- [jQuery](https://jquery.com)
- [Popper](https://popper.js.org)