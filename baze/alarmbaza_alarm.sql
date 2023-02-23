-- MySQL dump 10.13  Distrib 8.0.25, for Win64 (x86_64)
--
-- Host: localhost    Database: alarmbaza
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
-- Table structure for table `alarm`
--

DROP TABLE IF EXISTS `alarm`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alarm` (
  `id` int NOT NULL AUTO_INCREMENT,
  `h` int DEFAULT NULL,
  `min` int DEFAULT NULL,
  `ton` varchar(255) DEFAULT NULL,
  `vreme` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alarm`
--

LOCK TABLES `alarm` WRITE;
/*!40000 ALTER TABLE `alarm` DISABLE KEYS */;
INSERT INTO `alarm` VALUES (1,0,0,'https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze','2021-09-16 16:20:20'),(2,0,0,'https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze','2021-09-16 16:40:50'),(3,0,2,'https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze','2021-09-16 17:11:40'),(4,0,0,'https://www.youtube.com/watch?v=ieQ3LJYGK-g&ab_channel=ZeljkoVasic','2021-09-16 17:37:02'),(5,0,0,'https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze','2021-09-17 09:35:00'),(6,0,0,'https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze','2021-09-18 13:53:31'),(7,0,0,'https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze','2022-05-09 14:06:40'),(9,0,0,'https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze','2022-05-09 18:01:40'),(10,0,0,'https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze','2021-09-17 16:12:43'),(11,0,0,'https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze','2021-09-17 15:04:54'),(12,0,0,'https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze','2021-09-19 15:52:27'),(13,0,0,'https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze','2021-09-19 16:12:43'),(14,0,0,'https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze','2021-09-19 16:12:43'),(15,0,0,'https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze','2021-09-19 16:12:43'),(16,0,0,'https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze','2021-09-19 16:12:43'),(17,0,0,'https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze','2021-09-20 15:30:00'),(18,0,1,'https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze','2021-09-20 15:51:00'),(19,0,0,'https://www.youtube.com/watch?v=-vzYdtCDAfY&ab_channel=CRVENAJABUKA-CRORECOFFICIAL','2021-09-20 16:12:51'),(20,0,0,'https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze','2021-09-20 16:27:43'),(21,0,0,'https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze','2021-09-20 15:39:33'),(22,0,0,'https://www.youtube.com/watch?v=iNpXCzaWW1s&ab_channel=ArchilZambakhidze','1970-01-01 01:00:00');
/*!40000 ALTER TABLE `alarm` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-09-20 19:49:22
