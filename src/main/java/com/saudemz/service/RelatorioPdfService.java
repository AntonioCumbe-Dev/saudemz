
package com.saudemz.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import org.springframework.stereotype.Service;

import com.saudemz.model.Consulta;

@Service
public class RelatorioPdfService {

    private static final Color AZUL_ESCURO =
            new Color(24, 48, 78);

    private static final Color AZUL_CLARO =
            new Color(232, 240, 248);

    private static final Color CINZENTO =
            new Color(245, 247, 250);

    private static final Color BRANCO =
            Color.WHITE;

    public byte[] gerarRelatorioConsultas(
            Map<String, Object> relatorio) {

        ByteArrayOutputStream saida = new ByteArrayOutputStream();

        Document documento = new Document(
                PageSize.A4.rotate(), 35, 35, 35, 35
        );

        try {

            PdfWriter.getInstance(documento, saida);
            documento.open();

            Font titulo = FontFactory.getFont(
                    FontFactory.HELVETICA, 22, Font.BOLD, AZUL_ESCURO
            );

            Font subtitulo = FontFactory.getFont(
                    FontFactory.HELVETICA, 11, Font.NORMAL, AZUL_ESCURO
            );

            Font texto = FontFactory.getFont(
                    FontFactory.HELVETICA, 9, Font.NORMAL
            );

            Font destaque = FontFactory.getFont(
                    FontFactory.HELVETICA, 10, Font.BOLD, AZUL_ESCURO
            );

            Font cabecalhoTabela = FontFactory.getFont(
                    FontFactory.HELVETICA, 9, Font.BOLD, BRANCO
            );

            Font valorResumo = FontFactory.getFont(
                    FontFactory.HELVETICA, 15, Font.BOLD, AZUL_ESCURO
            );

            // CABEÇALHO

            Paragraph nomeClinica = new Paragraph(
                    "SAÚDE+ MZ", titulo
            );

            nomeClinica.setAlignment(Element.ALIGN_CENTER);
            nomeClinica.setSpacingAfter(5);

            documento.add(nomeClinica);

            Paragraph cabecalho = new Paragraph(
                    "RELATÓRIO DETALHADO DE CONSULTAS",
                    subtitulo
            );

            cabecalho.setAlignment(Element.ALIGN_CENTER);
            cabecalho.setSpacingAfter(25);

            documento.add(cabecalho);

            // PERÍODO

            documento.add(new Paragraph(
                    "PERÍODO DO RELATÓRIO", destaque
            ));

            LocalDateTime dataInicio =
                    (LocalDateTime) relatorio.get("dataInicio");

            LocalDateTime dataFim =
                    (LocalDateTime) relatorio.get("dataFim");

            DateTimeFormatter formatoData =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            documento.add(new Paragraph(
                    "Data inicial: " + dataInicio.format(formatoData),
                    texto
            ));

            documento.add(new Paragraph(
                    "Data final: " + dataFim.format(formatoData),
                    texto
            ));

            documento.add(new Paragraph(" "));

            // CONSULTAS

            @SuppressWarnings("unchecked")
            List<Consulta> consultas =
                    (List<Consulta>) relatorio.get("consultas");

            if (consultas == null) {
                throw new IllegalArgumentException(
                        "A lista de consultas não foi encontrada no relatório."
                );
            }

            // CONTAGEM

            int total = consultas.size();
            int marcadas = 0;
            int concluidas = 0;
            int canceladas = 0;

            for (Consulta consulta : consultas) {

                String estado = consulta.getEstado();

                if ("MARCADA".equalsIgnoreCase(estado)) {
                    marcadas++;
                } else if ("CONCLUIDA".equalsIgnoreCase(estado)) {
                    concluidas++;
                } else if ("CANCELADA".equalsIgnoreCase(estado)) {
                    canceladas++;
                }
            }

            // RESUMO

            documento.add(new Paragraph(
                    "RESUMO DAS CONSULTAS", destaque
            ));

            PdfPTable resumo = new PdfPTable(4);
            resumo.setWidthPercentage(100);
            resumo.setSpacingBefore(10);
            resumo.setSpacingAfter(20);

            int[] valores = {
                    total, marcadas, concluidas, canceladas
            };

            String[] etiquetas = {
                    "TOTAL", "MARCADAS", "CONCLUÍDAS", "CANCELADAS"
            };

            for (int i = 0; i < etiquetas.length; i++) {

                PdfPCell celula = new PdfPCell();

                celula.setBackgroundColor(AZUL_CLARO);
                celula.setBorderColor(BRANCO);
                celula.setPadding(12);
                celula.setHorizontalAlignment(Element.ALIGN_CENTER);

                Paragraph etiqueta = new Paragraph(
                        etiquetas[i], texto
                );

                etiqueta.setAlignment(Element.ALIGN_CENTER);

                Paragraph valor = new Paragraph(
                        String.valueOf(valores[i]), valorResumo
                );

                valor.setAlignment(Element.ALIGN_CENTER);

                celula.addElement(etiqueta);
                celula.addElement(valor);

                resumo.addCell(celula);
            }

            documento.add(resumo);

            // DETALHAMENTO

            documento.add(new Paragraph(
                    "DETALHAMENTO DAS CONSULTAS", destaque
            ));

            PdfPTable tabela = new PdfPTable(7);

            tabela.setWidthPercentage(100);
            tabela.setSpacingBefore(10);
            tabela.setSpacingAfter(15);

            tabela.setWidths(new float[] {
                    1.5f, 1.5f, 1.5f, 1.1f,
                    0.8f, 2.0f, 1.1f
            });

            tabela.setHeaderRows(1);

            String[] colunas = {
                    "Paciente",
                    "Médico",
                    "Especialidade",
                    "Data",
                    "Hora",
                    "Motivo",
                    "Estado"
            };

            for (String coluna : colunas) {

                PdfPCell celula = new PdfPCell(
                        new Phrase(coluna, cabecalhoTabela)
                );

                celula.setBackgroundColor(AZUL_ESCURO);
                celula.setHorizontalAlignment(Element.ALIGN_CENTER);
                celula.setVerticalAlignment(Element.ALIGN_MIDDLE);
                celula.setPadding(9);
                celula.setBorderColor(BRANCO);

                tabela.addCell(celula);
            }

            DateTimeFormatter formatoDia =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy");

            DateTimeFormatter formatoHora =
                    DateTimeFormatter.ofPattern("HH:mm");

            int linha = 0;

            for (Consulta consulta : consultas) {

                Color fundo = (linha % 2 == 0)
                        ? BRANCO
                        : CINZENTO;

                adicionarCelula(tabela,
                        consulta.getPaciente() != null
                                ? valorOuVazio(consulta.getPaciente().getNome())
                                : "Não indicado",
                        texto, fundo, Element.ALIGN_LEFT);

                adicionarCelula(tabela,
                        consulta.getMedico() != null
                                ? valorOuVazio(consulta.getMedico().getNome())
                                : "Não indicado",
                        texto, fundo, Element.ALIGN_LEFT);

                adicionarCelula(tabela,
                        consulta.getMedico() != null
                                ? valorOuVazio(consulta.getMedico().getEspecialidade())
                                : "Não indicada",
                        texto, fundo, Element.ALIGN_LEFT);

                adicionarCelula(tabela,
                        consulta.getDataHora() != null
                                ? consulta.getDataHora().format(formatoDia)
                                : "Não indicada",
                        texto, fundo, Element.ALIGN_CENTER);

                adicionarCelula(tabela,
                        consulta.getDataHora() != null
                                ? consulta.getDataHora().format(formatoHora)
                                : "Não indicada",
                        texto, fundo, Element.ALIGN_CENTER);

                adicionarCelula(tabela,
                        valorOuVazio(consulta.getMotivo()),
                        texto, fundo, Element.ALIGN_LEFT);

                adicionarCelula(tabela,
                        valorOuVazio(consulta.getEstado()),
                        texto, fundo, Element.ALIGN_CENTER);

                linha++;
            }

            if (consultas.isEmpty()) {

                PdfPCell vazia = new PdfPCell(
                        new Phrase(
                                "Não existem consultas para os filtros seleccionados.",
                                texto
                        )
                );

                vazia.setColspan(7);
                vazia.setPadding(12);
                vazia.setHorizontalAlignment(Element.ALIGN_CENTER);

                tabela.addCell(vazia);
            }

            documento.add(tabela);

            // RODAPÉ

            Paragraph rodape = new Paragraph(
                    "Documento gerado pelo sistema Saúde+ MZ.",
                    texto
            );

            rodape.setAlignment(Element.ALIGN_CENTER);
            rodape.setSpacingBefore(15);

            documento.add(rodape);

            documento.close();

            return saida.toByteArray();

        } catch (Exception erro) {

            if (documento.isOpen()) {
                documento.close();
            }

            throw new RuntimeException(
                    "Erro ao gerar o relatório PDF.",
                    erro
            );
        }
    }

    private void adicionarCelula(
            PdfPTable tabela,
            String valor,
            Font fonte,
            Color fundo,
            int alinhamento) {

        PdfPCell celula = new PdfPCell(
                new Phrase(valor, fonte)
        );

        celula.setBackgroundColor(fundo);
        celula.setHorizontalAlignment(alinhamento);
        celula.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celula.setPadding(7);
        celula.setBorderColor(new Color(220, 225, 232));

        tabela.addCell(celula);
    }

    private String valorOuVazio(String valor) {

        if (valor == null || valor.isBlank()) {
            return "Não indicado";
        }

        return valor;
    }
}
