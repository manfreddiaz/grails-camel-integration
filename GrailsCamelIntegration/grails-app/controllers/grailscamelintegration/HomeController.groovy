package grailscamelintegration

class HomeController {

    def index() { }
    def sendMeMessage() {
        def myMessage = [name:"foo",data:"bar"]
        sendMessage("seda:input.queue", myMessage)
    }
}
