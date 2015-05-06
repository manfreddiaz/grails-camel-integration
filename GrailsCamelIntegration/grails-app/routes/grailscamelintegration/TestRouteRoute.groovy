package grailscamelintegration



import org.apache.camel.builder.RouteBuilder

class TestRouteRoute extends RouteBuilder {
	def grailsApplication

    @Override
    void configure() {
		def config = grailsApplication?.config

        // example:
        from('seda:input.queue').to("bean:receivingService?method=serviceMethod")
    }
}
