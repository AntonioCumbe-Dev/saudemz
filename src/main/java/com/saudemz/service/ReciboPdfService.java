
package com.saudemz.service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfWriter;

import com.saudemz.model.Consulta;

@Service
public class ReciboPdfService {

    public byte[] gerarRecibo(Consulta consulta) {

        if (consulta == null
                || consulta.getPaciente() == null
                || consulta.getMedico() == null
                || consulta.getDataHora() == null) {
            throw new IllegalArgumentException(
                    "Os dados da consulta estão incompletos."
            );
        }

        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        Document documento = new Document();

        try {
            PdfWriter.getInstance(documento, saida);
            documento.open();

            Font titulo = FontFactory.getFont(
                    FontFactory.HELVETICA, 20, Font.BOLD
            );

            Font subtitulo = FontFactory.getFont(
                    FontFactory.HELVETICA, 12, Font.BOLD
            );

            Font texto = FontFactory.getFont(
                    FontFactory.HELVETICA, 11, Font.NORMAL
            );

            Paragraph cabecalho = new Paragraph("SAÚDE+ MZ", titulo);
            cabecalho.setAlignment(Element.ALIGN_CENTER);
            documento.add(cabecalho);

            Paragraph recibo = new Paragraph(
                    "COMPROVATIVO DE MARCAÇÃO DE CONSULTA",
                    subtitulo
            );
            recibo.setAlignment(Element.ALIGN_CENTER);
            recibo.setSpacingAfter(25);
            documento.add(recibo);

            documento.add(new Paragraph(
                    "Código da consulta: " + consulta.getCodigoConsulta(),
                    texto
            ));

            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("DADOS DO PACIENTE", subtitulo));

            documento.add(new Paragraph(
                    "Nome: " + consulta.getPaciente().getNome(), texto
            ));

            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("DADOS DA CONSULTA", subtitulo));

            documento.add(new Paragraph(
                    "Médico: " + consulta.getMedico().getNome(), texto
            ));

            documento.add(new Paragraph(
                    "Especialidade: "
                    + consulta.getMedico().getEspecialidade(),
                    texto
            ));

            documento.add(new Paragraph(
                    "Data: " + consulta.getDataHora().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    ),
                    texto
            ));

            documento.add(new Paragraph(
                    "Hora: " + consulta.getDataHora().format(
                            DateTimeFormatter.ofPattern("HH:mm")
                    ),
                    texto
            ));

            documento.add(new Paragraph(
                    "Estado: " + consulta.getEstado(), texto
            ));

            documento.add(new Paragraph(" "));

            Paragraph rodape = new Paragraph(
                    "Obrigado por escolher os serviços Saúde+ MZ.",
                    texto
            );
            rodape.setAlignment(Element.ALIGN_CENTER);
            documento.add(rodape);

            documento.close();

            return saida.toByteArray();

        } catch (Exception erro) {

            if (documento.isOpen()) {
                documento.close();
            }

            throw new RuntimeException(
                    "Erro ao gerar o recibo PDF da consulta.", erro
            );
        }
    }
}
