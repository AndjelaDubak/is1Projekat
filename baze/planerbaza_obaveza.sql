-- MySQL dump 10.13  Distrib 8.0.25, for Win64 (x86_64)
--
-- Host: localhost    Database: planerbaza
-- ------------------------------------------------------
-- Server version	8.0.25

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `obaveza`
--

DROP TABLE IF EXISTS `obaveza`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `obaveza` (
  `id` int NOT NULL AUTO_INCREMENT,
  `destinacija` varchar(255) DEFAULT NULL,
  `idA` int DEFAULT NULL,
  `opis` varchar(255) DEFAULT NULL,
  `start` varchar(255) DEFAULT NULL,
  `vreme` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `obaveza`
--

LOCK TABLES `obaveza` WRITE;
/*!40000 ALTER TABLE `obaveza` DISABLE KEYS */;
INSERT INTO `obaveza` VALUES (1,'Sombor',3,'okupljanje','Vrsac','2021-10-10 10:10:10'),(2,'Kragujevac',1,'zurka','x','2021-12-10 05:05:05'),(3,'Smederevo',NULL,'dogadjaj','x','2021-09-18 13:47:50'),(4,'Smederevo',6,'dog','x','2021-09-18 13:51:51'),(5,'Mladenovac',7,'dogadjaj','x','2022-05-09 14:05:00'),(7,'Smederevo',9,'dog','x','2022-05-09 18:00:00'),(8,'Mladenovac',10,'obavezaaaa','x','2021-09-17 17:00:00'),(9,'Kraljevo',11,'opisopis','x','2021-09-17 17:15:00'),(10,'Kragujevac',12,'opissss','Mladenovac','2021-09-19 17:00:00'),(11,'Mladenovac',20,'igra','Beograd','2021-09-20 17:15:00'),(12,'sombor',21,'dogadjaj','x','2021-09-20 18:00:00'),(13,'Valjevo',22,'obaveza','x','2021-09-20 19:00:00');
/*!40000 ALTER TABLE `obaveza` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-09-20 19:52:01
