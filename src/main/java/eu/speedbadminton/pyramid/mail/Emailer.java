package eu.speedbadminton.pyramid.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Sends and stores email messages
 *
 * @author Bastiaan Verhoef
 *
 */
public class Emailer {
    private static Logger LOGGER = Logger.getLogger(Emailer.class);
    private static final String MAIL_ADDRESS_SEPARATOR = ";";

    private Session session;
    private String emailFromAddress = "noreply@speedminton.eu";

    public Emailer() {
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            session = (Session) envCtx.lookup("mail/Session");
            emailFromAddress = session.getProperty("mail.from");
        } catch (NamingException ne) {
            LOGGER.error(ne.getMessage(), ne);
            throw new RuntimeException(ne);
        }

    }

    public Emailer(Session session) {
        this.session = session;
    }

    public void sendMessage(String toRecipients, String ccRecipients, String bccRecipients, String replyTo,
                            EmailComposer emailComposer) {
        try {
            int numberOfAttachments = emailComposer.getAttachments().size();
            LOGGER.info("Create new message: " + emailComposer.getSubject() + " with " + numberOfAttachments
                    + " attachments");
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("X-Priority", emailComposer.getPriority().toString());
            msg.setFrom(new InternetAddress(emailFromAddress));
            if (toRecipients != null) {
                msg.setRecipients(Message.RecipientType.TO, convertToInternetAddress(toRecipients));
            }
            if (ccRecipients != null) {
                msg.setRecipients(Message.RecipientType.CC, convertToInternetAddress(ccRecipients));
            }
            if (bccRecipients != null) {
                msg.setRecipients(Message.RecipientType.BCC, convertToInternetAddress(bccRecipients));
            }
            if (replyTo != null) {
                msg.setReplyTo(convertToInternetAddress(replyTo));
            }
            msg.setSubject(emailComposer.getSubject());
            MimeMultipart mimeMultipart = new MimeMultipart("mixed");

            /*
                * add html part
                */
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(emailComposer.getContent(), emailComposer
                    .getContentType())));
            mimeMultipart.addBodyPart(bodyPart);
            if (numberOfAttachments > 0) {
                for (DataSource dataSource : emailComposer.getAttachments()) {
                    // MimeBodyPart fileBodyPart = new MimeBodyPart();
                    MimeBodyPart mbp = new MimeBodyPart();
                    mbp.setDataHandler(new DataHandler(dataSource));
                    mbp.setFileName(dataSource.getName());
                    mbp.setDisposition(Part.ATTACHMENT);
                    // fileBodyPart.attachFile(file);
                    mimeMultipart.addBodyPart(mbp);
                }
                //
            }
            msg.setContent(mimeMultipart);
            msg.setSentDate(new Date());
            sendMessage(msg);
            LOGGER.info("The email has been sent");
        } catch (Exception e) {
            throw new RuntimeException("Unable to send email to " + toRecipients + " - " + e.getMessage(), e);
        }
    }

    private InternetAddress[] convertToInternetAddress(String recipients) throws AddressException {
        String[] toRecipientsArray = recipients.split(MAIL_ADDRESS_SEPARATOR);
        InternetAddress[] address = new InternetAddress[toRecipientsArray.length];
        for (int i = 0; i < toRecipientsArray.length; i++) {
            address[i] = new InternetAddress(toRecipientsArray[i]);
        }
        return address;
    }

    private void sendMessage(Message message) throws MessagingException, IOException {
        String mailToDirname = session.getProperty("mail.to.dir");
        Date currentDate = new Date();
        message.setSentDate(currentDate);
        if (StringUtils.isNotBlank(mailToDirname)) {
            File mailToDir = new File(mailToDirname);
            mailToDir.mkdirs();
            File mailToFile = new File(mailToDir, currentDate.getTime() + ".eml");
            FileOutputStream fileOutputStream = new FileOutputStream(mailToFile);
            message.writeTo(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } else {
            Transport.send(message);
        }
    }
}
