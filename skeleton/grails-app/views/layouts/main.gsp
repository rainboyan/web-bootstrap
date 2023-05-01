<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>
        <g:layoutTitle default="Grails"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <asset:link rel="icon" href="favicon.ico" type="image/x-ico"/>

    <asset:stylesheet src="application.css"/>

    <link id="bootswatch-style" rel="stylesheet" href=""/>

    <asset:stylesheet src="main.css"/>
    <asset:stylesheet src="grails.css"/>
    <asset:stylesheet src="mobile.css"/>

    <g:set var="layoutName" value="main"/>

    <g:layoutHead/>
</head>

<body>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top" role="navigation">
    <div class="container-fluid">
        <a class="navbar-brand" href="/"><asset:image src="grails.svg" alt="Grails Logo"/></a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent" aria-controls="navbarContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" aria-expanded="false" id="navbarContent">
            <ul class="navbar-nav ms-auto navbar-nav-scroll" style="--bs-scroll-height: 100px;">
                <g:if env="development">
                <li class="nav-item dropdown">
                    <a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Application Status</a>
                    <ul class="dropdown-menu">
                        <li><a class="dropdown-item" href="#">Environment: ${grails.util.Environment.current.name}</a></li>
                        <li><a class="dropdown-item" href="#">App profile: ${grailsApplication.config.grails?.profile}</a></li>
                        <li><a class="dropdown-item" href="#">App version:
                            <g:meta name="info.app.version"/></a>
                        </li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="#">Grails version:
                            <g:meta name="info.app.grailsVersion"/></a>
                        </li>
                        <li><a class="dropdown-item" href="#">Groovy version: ${GroovySystem.getVersion()}</a></li>
                        <li><a class="dropdown-item" href="#">JVM version: ${System.getProperty('java.version')}</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="#">Reloading active: ${grails.util.Environment.reloadingAgentEnabled}</a></li>
                    </ul>
                </li>
                <li class="nav-item dropdown">
                    <a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Artefacts</a>
                    <ul class="dropdown-menu">
                        <li><a class="dropdown-item" href="#">Controllers: ${grailsApplication.controllerClasses.size()}</a></li>
                        <li><a class="dropdown-item" href="#">Domains: ${grailsApplication.domainClasses.size()}</a></li>
                        <li><a class="dropdown-item" href="#">Services: ${grailsApplication.serviceClasses.size()}</a></li>
                        <li><a class="dropdown-item" href="#">Tag Libraries: ${grailsApplication.tagLibClasses.size()}</a></li>
                    </ul>
                </li>
                <li class="nav-item dropdown">
                    <a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Installed Plugins</a>
                    <ul class="dropdown-menu">
                        <g:each var="plugin" in="${applicationContext.getBean('pluginManager').allPlugins}">
                            <li><a class="dropdown-item" href="#">${plugin.name} - ${plugin.version}</a></li>
                        </g:each>
                    </ul>
                </li>
                </g:if>
                <li class="nav-item dropdown">
                    <a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Management</a>
                    <ul class="dropdown-menu">
                        <g:each var="c" in="${grailsApplication.controllerClasses.sort { it.name } }">
                            <li class="controller">
                                <g:link class="dropdown-item" controller="${c.logicalPropertyName}">
                                    <g:message code="${c.name.uncapitalize()}.label" default="${c.name}"/>
                                </g:link>
                            </li>
                        </g:each>
                    </ul>
                </li>
                <li class="nav-item dropdown">
                    <a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Languages</a>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <g:each var="lang" in="${['en', 'cs', 'da', 'de', 'es', 'fr', 'it', 'ja', 'nb', 'nl', 'pl', 'pt_BR', 'pt_PT', 'ru', 'sk', 'sv', 'th', 'zh_CN', 'zh_TW']}">
                            <g:set var="locale" value="${Locale.forLanguageTag(lang.replace('_', '-'))}"/>
                            <g:set var="paramsWithLang" value="${params + [lang:lang]}"/>
                            <li>
                                <g:link class="dropdown-item" action="${actionName}" params="${paramsWithLang}">
                                    ${locale.getDisplayName(locale)} - (${lang})
                                </g:link>
                            </li>
                        </g:each>
                    </ul>
                </li>
                <li class="nav-item dropdown">
                    <a id="bootswatch-navlink" href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Themes</a>
                    <ul id="bootswatch-themes" class="dropdown-menu dropdown-menu-end">
                        <li><a class="dropdown-item" href="#" onclick="changeTheme('default', '');return false;">Default</a></li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</nav>

<g:layoutBody/>

<div class="footer" role="contentinfo">
    <div class="container-fluid">
        <div class="row">
            <div class="col-md-4 col-sm-12">
                <a href="http://guides.grails.org" target="_blank">
                    <asset:image src="advancedgrails.svg" alt="Grails Guides" class="float-left"/>
                </a>
                <strong class="centered"><a href="http://guides.grails.org" target="_blank">Grails Guides</a></strong>
                <p>Building your first Grails app? Looking to add security, or create a Single-Page-App? Check out the <a href="http://guides.grails.org" target="_blank">Grails Guides</a> for step-by-step tutorials.</p>

            </div>
            <div class="col-md-4 col-sm-12">
                <a href="http://docs.grails.org" target="_blank">
                    <asset:image src="documentation.svg" alt="Grails Documentation" class="float-left"/>
                </a>
                <strong class="centered"><a href="http://docs.grails.org" target="_blank">Documentation</a></strong>
                <p>Ready to dig in? You can find in-depth documentation for all the features of Grails in the <a href="http://docs.grails.org" target="_blank">User Guide</a>.</p>

            </div>
            <div class="col-md-4 col-sm-12">
                <a href="https://slack.grails.org" target="_blank">
                    <asset:image src="slack.svg" alt="Grails Slack" class="float-left"/>
                </a>
                <strong class="centered"><a href="https://slack.grails.org" target="_blank">Join the Community</a></strong>
                <p>Get feedback and share your experience with other Grails developers in the community <a href="https://slack.grails.org" target="_blank">Slack channel</a>.</p>
            </div>
        </div>
    </div>
</div>

<div id="spinner" class="spinner" style="display:none;">
    <div class="d-flex justify-content-center">
        <div class="spinner-border text-primary" role="status">
            <span class="sr-only">Loading...</span>
        </div>
    </div>
</div>

<asset:javascript src="application.js"/>

<g:if test='${asset.assetPathExists(src: "${layoutName}.js")}'>
    <asset:javascript src="${layoutName}.js"/>
</g:if>
<g:if test='${asset.assetPathExists(src: "${controllerName}.js")}'>
    <asset:javascript src="${controllerName}.js"/>
</g:if>
<g:if test='${asset.assetPathExists(src: "${controllerName}-${actionName}.js")}'>
    <asset:javascript src="${controllerName}-${actionName}.js"/>
</g:if>

</body>
</html>
