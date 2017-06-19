@artifact.package@

import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

class @artifact.name@TagLibSpec extends Specification implements TagLibUnitTest<@artifact.name@TagLib> {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        expect:"fix me"
            true == false
    }
}
