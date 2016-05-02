package stans;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;


/**
 *
 * @author jwatson
 */
public class EasyMailer {

    private String toAddress;
    private String receiptToAddress;
    private String fromAddress;
    private String ccAddress;
    private ArrayList<String> toAddresses;
    private ArrayList<String> ccAddresses;
    private ArrayList<String> bccAddresses;
    private String replyToAddress;
    private String host;
    private String body;
    private String subject;
    private EasyMailer receipt = null;

    public EasyMailer() {
        //set defaults        
        host = "mail-relay1-cal1.blackboard.com";
        //host = "mail.edonline.sk.ca";
        //host = "mail2.gov.sk.ca";
        toAddress = "ctwelihinda@gmail.com";
        fromAddress = "do-not-reply@merlin.ca";
        ccAddress = "";
        toAddresses = new ArrayList<String>();
        ccAddresses = new ArrayList<String>();
        bccAddresses = new ArrayList<String>();
        replyToAddress = "";
    }

    public void setTo(String to) {
        toAddress = to;
    }
	
    public void setFrom(String from) {
        fromAddress = from;
    }

    public void setCc(String cc) {
        ccAddress = cc;
    }
	
	public void addAddressToToList( String to ) {
		toAddresses.add( to );
	}
    public void addAddressToCcList( String cc ) {
        ccAddresses.add( cc );
    }
    public void addAddressToBccList( String bcc ) {
        bccAddresses.add( bcc );
    }

    public void setReplyTo(String replyTo) {
        replyToAddress = replyTo;
    }

    public void setSubject(String subject_html) {
        subject = subject_html;
    }

    public void setBody(String body_html) {
        body = body_html;
    }

    public String getTo() {
        return toAddress;
    }

    public String getFrom() {
        return fromAddress;
    }

    public String getCc() {
        return ccAddress;
    }

    public String getReplyTo() {
        return replyToAddress;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getHtmlSafeBody() {
        return getBody().replaceAll("(?s)cid:ministry_email_icon_6745231zxc", "images/ministry_email.png");
    }

    public String getReceiptToAddress() {
        return receiptToAddress;
    }

    public EasyMailer getReceipt() {
        return receipt;
    }

    public void setReceipt(EasyMailer receiptEmail) {
        receipt = receiptEmail;
    }

    public void setReport(EasyEmailReport report) {
        toAddress = report.getTo();
        fromAddress = report.getFrom();
        ccAddress = report.getCc();
        replyToAddress = report.getReplyTo();
        body = report.getBody();
        subject = report.getSubject();

        if (report.getReceipt() != null) {
            receipt = new EasyMailer();
            receipt.setTo(report.getReceipt().getTo());
            receipt.setCc(report.getReceipt().getCc());
            receipt.setReplyTo(report.getReceipt().getReplyTo());
            receipt.setFrom(report.getReceipt().getFrom());
            receipt.setBody(report.getReceipt().getBody() + body);
            receipt.setSubject(report.getReceipt().getSubject());
        }

    }

    public void overideEmailHost(String h) {
        host = h;
    }

    // execute the email
    public void send() throws MessagingException {

        Multipart multipart = new MimeMultipart("related");

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(body, "text/html");
        multipart.addBodyPart(messageBodyPart);

        MimeBodyPart iconBodyPart = new MimeBodyPart();


        File image;
        try {      
            image = new File(getClass().getResource("ministry_email.png").getPath());
          //  image = new File(((new Object())).getClass().getResource("/images/ministry_email.png").toURI);
            DataSource iconDataSource = new FileDataSource(image);
            iconBodyPart.setDataHandler(new DataHandler(iconDataSource));
            iconBodyPart.setDisposition(Part.INLINE);
            iconBodyPart.setContentID("<ministry_email_icon_6745231zxc>");
            iconBodyPart.addHeader("Content-Type", "image/png");
            multipart.addBodyPart(iconBodyPart);
        } catch (Exception e) {
            Logger.getLogger(EasyMailer.class.getName()).log(Level.SEVERE, null, e);
        }

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);

        Session session = Session.getDefaultInstance(properties);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromAddress));
		
		if( !toAddress.equals("") ) {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
		}

        if (replyToAddress != null && !replyToAddress.equals("")) {
            InternetAddress[] reply_tos = new InternetAddress[1];
            reply_tos[0] = new InternetAddress(replyToAddress);
            message.setReplyTo(reply_tos);
        }
        if (ccAddress != null && !ccAddress.equals("")) {
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccAddress));
        }
		
        for( String to : toAddresses ) {
            message.addRecipient( Message.RecipientType.TO, new InternetAddress( to ) );
        }
        for( String cc : ccAddresses ) {
            message.addRecipient( Message.RecipientType.CC, new InternetAddress( cc ) );
        }
        for( String bcc : bccAddresses ) {
            message.addRecipient( Message.RecipientType.BCC, new InternetAddress( bcc ) );
        }

        message.setSubject(subject);
        message.setContent(multipart);

        Transport.send(message);

        // if a receipt address was included send the original message to 
        // the receipt address prepended by the receipt text
        if (receipt != null) {

            receipt.send();
        }

    }
}
