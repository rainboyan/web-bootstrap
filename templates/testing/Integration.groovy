@artifact.package@

import grails.testing.mixin.integration.Integration
import grails.transaction.*
import spock.lang.*

@Integration
@Rollback
class @artifact.name@Spec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        expect:"fix me"
            true == false
    }
}
