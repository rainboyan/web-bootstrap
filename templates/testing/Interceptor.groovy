@artifact.package@
import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class @artifact.name@InterceptorSpec extends Specification implements InterceptorUnitTest<@artifact.name@Interceptor> {

    def setup() {
    }

    def cleanup() {

    }

    void "Test @artifact.propertyName@ interceptor matching"() {
        when:"A request matches the interceptor"
            withRequest(controller:"@artifact.propertyName@")

        then:"The interceptor does match"
            interceptor.doesMatch()
    }
}
