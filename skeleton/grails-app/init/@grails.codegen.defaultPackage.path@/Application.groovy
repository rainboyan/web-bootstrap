package @grails.codegen.defaultPackage@

import grails.boot.Grails

import groovy.transform.CompileStatic

@CompileStatic
class Application {

    static void main(String[] args) {
        Grails.run(Application, args)
    }

}
