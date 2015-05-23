package grailscamelintegration



import org.apache.camel.builder.RouteBuilder

class TestRouteRoute extends RouteBuilder {
	def grailsApplication

    @Override
    void configure() {
		def config = grailsApplication?.config

        String Sender = "oaddam";
        String Domain = "@gomentr.com";
        String Password = "mathyboy99";

        String senderMailServer = "smtp.gmail.com";
        String senderMailServerPort = "465";

        String receiverMailServer = "imap.gmail.com";

        //Email sending
        //This will be called when u click on the send button
        String emailSender = prepareEmailSenderEndPoint(Sender, Domain, Password, senderMailServer, senderMailServerPort,
                "omaddam@gmail.com", "Subject: Testing the email sender", "Body: testing the email sender", "1")
        println(emailSender)
        from("seda:input.queue").to(emailSender);

        //Emails receiving
        //This will be called once every one minute and when it receives any email, it will forward them one by one to
        //receiveEmailServiceMethod [method] found in receivingService [service]
        String emailReceiver = prepareEmailReceiverEndPoint(Sender, Domain, Password, receiverMailServer, 60000)
        println(emailReceiver)
        //from(emailReceiver).to("bean:receivingService?method=receiveEmailServiceMethod");
    }

    /**
     * This method prepares the EndPoint that will be used by the camel apache library to send an email
     * @param Sender: represents the first part of the email that will be used as the sender (ex: oaddam)
     * @param Domain: represents the second part of the email that will be used as the sender (ex: @gomentr.com)
     * @param Password: the password that will be used for authenticating the sender email provided in the two previous parameters (oaddam@gomentr.com)
     * @param MailServer: the mail server that will handle the sending of the email (ex: smtp.gmail.com)
     * @param MailServerPort: the port that is opened at the mail server to accept sending requests (ex: 465)
     * @param ToEmail: the email of the receipt (ex: omaddam@gmail.com)
     * @param Subject: the subject part of the email
     * @param Body: the body part of the email
     * @param NotificationID: an ID that can be provided and allow the user to reply to a specific event, this ID will be retrieved when the user replies to our email, and it can be null
     * @return
     */
    private String prepareEmailSenderEndPoint(String Sender, String Domain, String Password, String MailServer, String MailServerPort,
                                              String ToEmail, String Subject, String Body,
                                              String NotificationID) {
        String EmailSenderEndPoint =
                "smtps://" + MailServer + ":" +  MailServerPort + "?" +
                "username=" + Sender + (NotificationID ? ("+" + NotificationID) : "") + Domain +
                "&password=" + Password +
                "&To=" + ToEmail +
                "&Subject=" + Subject +
                "&debugMode=false"

        return EmailSenderEndPoint;
    }

    /**
     * This method prepares the EndPoint that will be used by the camel apache library to send an email
     * @param Receiver: represents the first part of the email that will be used as the receiver (ex: oaddam)
     * @param Domain: represents the second part of the email that will be used as the receiver (ex: @gomentr.com)
     * @param Password: the password that will be used for authenticating the receiver email provided in the two previous parameters (oaddam@gomentr.com)
     * @param MailServer: the mail server that will handle the receiving of the emails (ex: imap.gmail.com)
     * @param Delay: the delay in milli-seconds as a periodic scheduler to fetch the emails
     * @return
     */
    private String prepareEmailReceiverEndPoint(String Receiver, String Domain, String Password, String MailServer, int Delay) {
        String EmailReceiverEndPoint =
                "imaps://" + MailServer + "?" +
                "username=" + Receiver + Domain +
                "password=" + Password +
                "password=" + Password +
                "&unseen=true" +
                "&consumer.delay=" + Delay.toString() +
                "&debugMode=false"

        return EmailReceiverEndPoint;
    }

}