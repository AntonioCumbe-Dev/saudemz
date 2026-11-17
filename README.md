# Saúde+ MZ

### Sistema de Gestão de Consultas Médicas

O **Saúde+ MZ** é uma aplicação web desenvolvida para apoiar a gestão dos serviços de atendimento em clínicas privadas em Moçambique.

O projecto procura simplificar os processos de gestão de médicos, pacientes e consultas, oferecendo uma plataforma centralizada, com autenticação e funcionalidades de apoio à administração clínica.

## Objectivo do Projecto

Desenvolver uma solução informática que contribua para a organização dos processos de atendimento médico, facilitando a gestão de consultas e o acesso à informação.

## Funcionalidades

- Gestão de médicos e respectivas especialidades.
- Registo e gestão de pacientes.
- Agendamento de consultas médicas.
- Confirmação, reagendamento e cancelamento de consultas.
- Controlo do estado das consultas.
- Autenticação e controlo de acesso por perfis.
- Gestão de contas de utilizadores.
- Geração de comprovativos em PDF.
- Envio de notificações por email.
- Consulta de informações e relatórios administrativos.

## Tecnologias Utilizadas

| Tecnologia | Aplicação |
|---|---|
| Java 25 | Linguagem de programação |
| Spring Boot 4.1.1 | Framework principal |
| Spring Data JPA | Persistência e acesso a dados |
| Spring Security | Autenticação e segurança |
| Spring WebMVC | Desenvolvimento web |
| Spring Mail | Envio de emails |
| MySQL | Sistema de gestão de base de dados |
| OpenPDF 2.4.0 | Geração de documentos PDF |
| Maven | Gestão de dependências e construção do projecto |
| HTML, CSS e JavaScript | Interface web |

## Arquitectura e Organização

O projecto segue uma organização por camadas, separando responsabilidades para facilitar a manutenção e evolução da aplicação.

Estrutura principal:

```text
saudemz/
├── src/
│   ├── main/
│   │   ├── java/com/saudemz/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   │       └── static/
│   └── test/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Requisitos

- JDK 25.
- MySQL Server.
- Maven ou Maven Wrapper incluído no projecto.
- IDE de desenvolvimento Java, como Spring Tool Suite ou IntelliJ IDEA.

## Configuração e Execução

### 1. Clonar o repositório

```bash
git clone https://github.com/AntonioCumbe-Dev/saudemz.git
```

### 2. Entrar na pasta do projecto

```bash
cd saudemz
```

### 3. Preparar a base de dados

Criar a base de dados MySQL:

```sql
CREATE DATABASE saudemz_db;
```

O nome correcto da base de dados é `saudemz_db`.

### 4. Configurar a aplicação

Configurar os dados de ligação à base de dados e as credenciais de email através das propriedades locais da aplicação e das variáveis de ambiente necessárias.

Não colocar palavras-passe, tokens ou credenciais reais no repositório.

### 5. Executar a aplicação

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

## Segurança

A aplicação utiliza Spring Security para autenticação e controlo de acesso.

As credenciais e configurações sensíveis devem ser fornecidas através de variáveis de ambiente ou configuração local, não devendo ser publicadas no GitHub.

## Estado do Projecto

**Em desenvolvimento — MVP.**

O Saúde+ MZ encontra-se em evolução, com foco na consolidação das funcionalidades de gestão clínica, segurança e experiência de utilização.

## Autor

**António Cumbe**

Desenvolvimento de Software | Java | Spring Boot

## Licença

Este projecto encontra-se em desenvolvimento. Os termos de utilização e distribuição deverão ser definidos numa futura versão.
