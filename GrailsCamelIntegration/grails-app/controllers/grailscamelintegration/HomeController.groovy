package grailscamelintegration

class HomeController {

    def index() { }
    def sendMeMessage() {

        def myMessage = []
        sendMessage("seda:input.queue", myMessage)
    }
}
