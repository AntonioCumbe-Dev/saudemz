
package com.saudemz.service;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ENVIAR EMAIL SIMPLES
    public void enviarEmail(
            String destinatario,
            String assunto,
            String mensagem) {

        SimpleMailMessage email = new SimpleMailMessage();

        email.setTo(destinatario);
        email.setSubject(assunto);
        email.setText(mensagem);

        mailSender.send(email);
    }

    // NOTIFICAR REAGENDAMENTO
    public void notificarConsultaReagendada(
            String destinatario,
            String nomePaciente,
            String nomeMedico,
            String novaDataHora) {

        String mensagem =
                "Olá, " + nomePaciente + "!\n\n"
                + "A sua consulta foi reagendada com sucesso.\n\n"
                + "Médico: " + nomeMedico + "\n"
                + "Nova data e hora: " + novaDataHora + "\n\n"
                + "Saúde+ MZ - Cuidar de si é a nossa prioridade.";

        enviarEmail(destinatario,
                "Consulta reagendada - Saúde+ MZ",
                mensagem);
    }

    // NOTIFICAR CANCELAMENTO
    public void notificarConsultaCancelada(
            String destinatario,
            String nomePaciente,
            String nomeMedico) {

        String mensagem =
                "Olá, " + nomePaciente + "!\n\n"
                + "Informamos que a sua consulta foi cancelada.\n\n"
                + "Médico: " + nomeMedico + "\n\n"
                + "Para mais informações, contacte a clínica.\n\n"
                + "Saúde+ MZ.";

        enviarEmail(destinatario,
                "Consulta cancelada - Saúde+ MZ",
                mensagem);
    }

    // NOTIFICAR CONCLUSÃO
    public void notificarConsultaConcluida(
            String destinatario,
            String nomePaciente,
            String nomeMedico) {

        String mensagem =
                "Olá, " + nomePaciente + "!\n\n"
                + "A sua consulta foi registada como concluída.\n\n"
                + "Médico: " + nomeMedico + "\n\n"
                + "Obrigado por escolher a Saúde+ MZ.\n\n"
                + "Cuidar de si é a nossa prioridade.";

        enviarEmail(destinatario,
                "Consulta concluída - Saúde+ MZ",
                mensagem);
    }

    // ENVIAR EMAIL COM ANEXO PDF
    public void enviarEmailComPdf(
            String destinatario,
            String assunto,
            String mensagem,
            byte[] pdf,
            String nomeFicheiro) throws MessagingException {

        MimeMessage email = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(email, true, "UTF-8");

        helper.setTo(destinatario);
        helper.setSubject(assunto);
        helper.setText(mensagem);

        InputStreamSource anexo =
                () -> new ByteArrayInputStream(pdf);

        helper.addAttachment(
                nomeFicheiro, anexo, "application/pdf");

        mailSender.send(email);
    }
}
