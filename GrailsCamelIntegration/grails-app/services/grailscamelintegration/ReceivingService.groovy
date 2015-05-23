package grailscamelintegration

import grails.transaction.Transactional
import org.apache.camel.Exchange

import javax.mail.Address
import javax.mail.Message
import org.apache.camel.component.mail.MailMessage

import javax.mail.MessagingException
import javax.mail.Multipart
import javax.mail.Part
import javax.mail.internet.MimeMultipart
import java.util.regex.Matcher
import java.util.regex.Pattern

@Transactional
class ReceivingService {

    def localServiceMethod() {
        println 'Got my local message'
    }
    def receiveEmailServiceMethod(Exchange exchange) {

        //Retrieve the message
        Message mailMessage = ((MailMessage) exchange.getIn()).getMessage();

        //Parse
        String From = mailMessage.getFrom().first().toString()
        String To = mailMessage.getAllRecipients().first().toString()
        String NotificationID = ProcessNotificationID(mailMessage.getAllRecipients().first())

        Date ReceivedDate = mailMessage.getReceivedDate()
        Date SentDate = mailMessage.getSentDate()

        String Subject = mailMessage.getSubject()
        String Content = ProcessMessageContent(mailMessage.getContent())?: 'N/A'

        //Display
        println("##################################")
        println("From: " + From)
        println("To: " + To)
        println("Notification ID: " + NotificationID)
        println()
        println("Received Date: " + ReceivedDate)
        println("Sent Date: " + SentDate)
        println()
        println("Subject: " + Subject)
        println("Content: " + Content)
        println("##################################")

    }

    /**
     * Processes the ID passed by the email sender and collected by email reader
     * By default, it extracts it from the reply email
     * The default pattern is /\+[A-Za-z0-9-_]*\@/: GUID
     */
    protected String ProcessNotificationID(Address toAddress)
            throws Exception {

        String patternString = "\\+[A-Za-z0-9-_]*@";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(toAddress.toString());
        boolean matches = matcher.find(0);

        if (!matches)
            return null;

        String id = matcher.group(0).substring(1, matcher.group(0).length() - 1);
        return id;
    }

    /**
     * The two methods below will retrieve the body of the message as a string
     */
    protected String ProcessMessageContent(Object msgContent)
            throws Exception {
        String content = null;
        if (msgContent instanceof Multipart) {
            Multipart multipart = (Multipart) msgContent;
            for (int j = 0; j < multipart.getCount(); j++)
                content = getText(multipart.getBodyPart((j)));
        }
        else
            content = Message.getContent().toString();
        return content;
    }
    private String getText(Part p) throws
            MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String)p.getContent();
            boolean textIsHtml = p.isMimeType("text/html");
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }
        return null;
    }

}
