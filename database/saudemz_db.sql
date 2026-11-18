-- MariaDB dump 10.19  Distrib 10.4.32-MariaDB, for Win64 (AMD64)
--
-- Host: 127.0.0.1    Database: saudemz_db
-- ------------------------------------------------------
-- Server version	10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `saudemz_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `saudemz_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;

USE `saudemz_db`;

--
-- Table structure for table `consulta`
--

DROP TABLE IF EXISTS `consulta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `consulta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data_hora` datetime(6) DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `motivo` varchar(255) DEFAULT NULL,
  `medico_id` bigint(20) DEFAULT NULL,
  `paciente_id` bigint(20) DEFAULT NULL,
  `lembrete_enviado` tinyint(1) NOT NULL DEFAULT 0,
  `codigo_consulta` varchar(40) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_consulta_codigo` (`codigo_consulta`),
  KEY `FKskwvuuev2optdrjsfeo1pqo83` (`medico_id`),
  KEY `FK1oq22nlbbvpijm5n2t6vqi1is` (`paciente_id`),
  CONSTRAINT `FK1oq22nlbbvpijm5n2t6vqi1is` FOREIGN KEY (`paciente_id`) REFERENCES `paciente` (`id`),
  CONSTRAINT `FKskwvuuev2optdrjsfeo1pqo83` FOREIGN KEY (`medico_id`) REFERENCES `medico` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consulta`
--

LOCK TABLES `consulta` WRITE;
/*!40000 ALTER TABLE `consulta` DISABLE KEYS */;
INSERT INTO `consulta` VALUES (1,'2026-10-15 09:00:00.000000','CONCLUIDA','Consulta de rotina',1,1,0,'SAUDE-BAAC4E0E'),(2,'2026-10-28 09:36:00.000000','CONCLUIDA','ja estou a duas semanas com dores de estomago.',3,1,0,'SAUDE-BAAC5AE5'),(3,'2026-02-24 13:56:00.000000','CONCLUIDA','estou a 1 mes com uma pontada no coração.',4,4,0,'SAUDE-BAAC5C3D'),(4,'2026-10-20 10:00:00.000000','CANCELADA','Teste de marcacao',1,1,0,'SAUDE-BAAC5D1B'),(5,'2026-10-21 10:00:00.000000','CANCELADA','Teste de reutilizacao de horario',1,3,0,'SAUDE-BAAC5DED'),(6,'2026-10-21 19:30:00.000000','CONCLUIDA','Teste da ultima hora permitida',3,4,0,'SAUDE-BAAC5EB8'),(7,'2026-10-21 10:00:00.000000','CONCLUIDA','Confirmacao de horario libertado',1,3,0,'SAUDE-BAAC5F7F'),(8,'2026-10-22 11:00:00.000000','CANCELADA','Estou praticamente a dois meses com dor de estomago.',5,4,0,'SAUDE-BAAC6049'),(9,'2026-10-22 11:30:00.000000','CONCLUIDA','estou com dores de cabeca.',3,4,0,'SAUDE-BAAC6112'),(10,'2026-10-21 10:00:00.000000','CONCLUIDA','dores de estomago.',4,1,0,'SAUDE-BAAC61D2'),(11,'2026-10-21 10:00:00.000000','CONCLUIDA','dores de estomago.',5,5,0,'SAUDE-BAAC6295'),(12,'2026-11-18 06:00:00.000000','CANCELADA','Dores estomacais.',3,6,0,'SAUDE-BAAC6355'),(13,'2026-11-02 06:30:00.000000','CONCLUIDA','Estou com dores de Estomago.',4,6,0,'SAUDE-BAAC6415'),(14,'2026-11-03 09:00:00.000000','CONCLUIDA','Dores nasais.',6,6,0,'SAUDE-BAAC64D7'),(15,'2026-11-05 06:00:00.000000','CONCLUIDA','Dores estomacais.',4,7,0,'SAUDE-D695F3B6'),(16,'2026-11-09 06:00:00.000000','CANCELADA','Estou a uma semana com dores na garganta.',4,8,0,'SAUDEMZ-09111'),(17,'2026-11-15 10:00:00.000000','MARCADA','Dores de cabeca intensos',1,8,0,'SAUDEMZ-0911002'),(18,'2026-11-15 10:00:00.000000','MARCADA','Dores de cabeca intensos',1,8,0,'SAUDEMZ-0911003'),(19,'2026-11-15 10:00:00.000000','MARCADA','Dores de cabeca intensos',1,8,0,'SAUDEMZ-0911004'),(20,'2026-11-15 11:30:00.000000','CANCELADA','Dores de cabeca intensos',1,9,0,'SAUDEMZ-1011001'),(21,'2026-11-15 11:30:00.000000','MARCADA','Dores de cabeca intensos',1,9,0,'SAUDEMZ-1011002'),(22,'2026-11-15 11:30:00.000000','MARCADA','Dores de cabeca intensos',1,9,0,'SAUDEMZ-1011003'),(23,'2026-11-15 09:00:00.000000','CANCELADA','Estou a Três meses sofrendo de insónia.',1,10,0,'SAUDEMZ-1011004'),(24,'2026-11-15 08:30:00.000000','MARCADA','Estou a Três meses sofrendo de insónia.',1,10,0,'SAUDEMZ-1011005');
/*!40000 ALTER TABLE `consulta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `disponibilidade_medico`
--

DROP TABLE IF EXISTS `disponibilidade_medico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `disponibilidade_medico` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `medico_id` bigint(20) NOT NULL,
  `dia_semana` varchar(20) NOT NULL,
  `hora_inicio` time NOT NULL,
  `hora_fim` time NOT NULL,
  `disponivel` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `fk_disponibilidade_medico` (`medico_id`),
  CONSTRAINT `fk_disponibilidade_medico` FOREIGN KEY (`medico_id`) REFERENCES `medico` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `disponibilidade_medico`
--

LOCK TABLES `disponibilidade_medico` WRITE;
/*!40000 ALTER TABLE `disponibilidade_medico` DISABLE KEYS */;
INSERT INTO `disponibilidade_medico` VALUES (1,1,'SUNDAY','06:30:00','12:30:00',1),(2,7,'MONDAY','08:30:00','16:30:00',1);
/*!40000 ALTER TABLE `disponibilidade_medico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medico`
--

DROP TABLE IF EXISTS `medico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medico` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `especialidade` varchar(255) DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `telefone` varchar(255) DEFAULT NULL,
  `usuario_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_medico_usuario` (`usuario_id`),
  CONSTRAINT `fk_medico_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medico`
--

LOCK TABLES `medico` WRITE;
/*!40000 ALTER TABLE `medico` DISABLE KEYS */;
INSERT INTO `medico` VALUES (1,'carlos@gmail.com','Neurologia','Carlos Manuel','841234567',NULL),(3,'celesia@gmail.com','Neurologia','Celesia Joaquim Tsamba','875332687',2),(4,'cumbeantonio292@gmail.com','Cardiologia','António Cumbe','844848540',NULL),(5,'jose@gmail.com','Neurologia','Jose Nhabanga','847648439',NULL),(6,'silvia@gmail.com','Neurologia','Silvia Perreira','87484748333',3),(7,'celine@gmail.com','CVardiologia','Celia Manhisse','842022281',7);
/*!40000 ALTER TABLE `medico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `paciente`
--

DROP TABLE IF EXISTS `paciente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `paciente` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data_nascimento` date DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `endereco` varchar(255) DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `sexo` enum('FEMININO','MASCULINO') DEFAULT NULL,
  `telefone` varchar(255) DEFAULT NULL,
  `usuario_id` bigint(20) DEFAULT NULL,
  `data_registo` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `fk_paciente_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `paciente`
--

LOCK TABLES `paciente` WRITE;
/*!40000 ALTER TABLE `paciente` DISABLE KEYS */;
INSERT INTO `paciente` VALUES (1,'2000-05-15','carlos@example.com','Maputo','Carlos Manuel','MASCULINO','849876543',NULL,NULL),(3,'1951-11-11','aldaduce@gmail.com','Polana Caniço, Q.20, casa n.52','Alda Duce','FEMININO','873847494',NULL,NULL),(4,'2026-10-14','brunomanjate@gmail.com','bairro ferroviário, Q.10, casa n.21','Bruno Manjate','MASCULINO','8574484976',NULL,NULL),(5,'1980-05-12','matildecumbe@gmail.com','Bairro: Matendene, Q.20, casa n. 10','Matilde Cumbe','FEMININO','875070976',NULL,NULL),(6,'1951-01-01','lourenco@gmail.com','bairro boquisso, Q.20, casa n.52','Lourenco Manhisse','MASCULINO','843985855',4,NULL),(7,'1989-08-11','celesiajoaquim81@gmail.com','Polana Canico, Q.18, casa n120','Celesia Joaquim','FEMININO','875333835',5,NULL),(8,'2007-03-18','cumbeleo7@gmail.com','Bairro Guava, Q.20, casa n.55','Lourenco Cumbe','MASCULINO','849637554',6,'2026-11-08'),(9,'1998-05-14','maria.mucavele26@gmail.com','Bairro da Matola','Maria Celina Mucavele','FEMININO','841234567',8,'2026-11-10'),(10,'1993-10-12','brunomanjate2@gmail.com','Bairro da Matola, Q.20, casa n.57','Bruno Francisco Manjate','MASCULINO','869079473',9,'2026-11-10');
/*!40000 ALTER TABLE `paciente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sequencia_consulta`
--

DROP TABLE IF EXISTS `sequencia_consulta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequencia_consulta` (
  `data_codigo` char(4) NOT NULL,
  `ultimo_numero` int(11) NOT NULL,
  PRIMARY KEY (`data_codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequencia_consulta`
--

LOCK TABLES `sequencia_consulta` WRITE;
/*!40000 ALTER TABLE `sequencia_consulta` DISABLE KEYS */;
INSERT INTO `sequencia_consulta` VALUES ('0911',4),('1011',5);
/*!40000 ALTER TABLE `sequencia_consulta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuarios` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nome` varchar(150) NOT NULL,
  `email` varchar(150) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `perfil` varchar(30) NOT NULL,
  `ativo` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'Administrador','admin@saudemz.co.mz','$2a$10$H0nahFO98KCllQHgm60YROhpZPlb3mSW0Juyj/ODPzDD.jMkQ.nuq','SUPERUTILIZADOR',1),(2,'Celesia','celesia@gmail.com','$2a$10$mo21E66xRtUoVgFdhKNp5uANbCVAsqgkAlU8V3otGbGbWiEQHVjNe','MEDICO',1),(3,'Silvia','silvia@gmail.com','$2a$10$USmffEU7uiAOHFeRw0oUO.Dar3nO.x.KHxxQnj.JbE7pj1YtF17bW','MEDICO',1),(4,'Lourenco Manhisse','lourenco@gmail.com','$2a$10$l0yrNBMldU97e7u9qkcDw.Xm/rqeyWiYrL1MRHy1mrkPHLJtq/qsG','PACIENTE',1),(5,'Celesia Joaquim','celesiajoaquim81@gmail.com','$2a$10$tnhIcr9bnany4C8fnH/UTOAaVT6a0XIjrnKdMs9kGfrOFHb1wopgm','PACIENTE',1),(6,'Lourenco Cumbe','cumbeleo7@gmail.com','$2a$10$SVxq7KkUehhNBA5ZTQtVQ.mnFSDoUwQNb.UrVtSCAuDwTFhwMJU9G','PACIENTE',1),(7,'Celia Manhisse','celine@gmail.com','$2a$10$Lxh6QHUfxzbSduPi9KnRf.Ow6F82rMIGv6jlLctRlAP4cy1o/ZyAi','MEDICO',1),(8,'Maria Celina Mucavele','maria.mucavele26@gmail.com','$2a$10$/AmaKvfEiSDFbEXveKRnCOYdYDGSvihqS2BMp.5HiRLE4Ejs3qTIS','PACIENTE',1),(9,'Bruno Francisco Manjate','brunomanjate2@gmail.com','$2a$10$JEZ5ILkkZXu7QIvF6xvUQu3PEHrxAraxnDzWABZFnK0/qKSBl4MVa','PACIENTE',1);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'saudemz_db'
--

--
-- Dumping routines for database 'saudemz_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-17 21:55:21
