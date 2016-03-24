-- phpMyAdmin SQL Dump
-- version 4.2.12deb1
-- http://www.phpmyadmin.net
--
-- Client :  localhost
-- Généré le :  Jeu 24 Mars 2016 à 15:00
-- Version du serveur :  5.5.40-0+wheezy1
-- Version de PHP :  5.6.2-1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de données :  `JUnitHistoryTools`
--

-- --------------------------------------------------------

--
-- Structure de la table `tcomment`
--

CREATE TABLE IF NOT EXISTS `tcomment` (
  `id` int(11) NOT NULL,
  `title` varchar(256) NOT NULL,
  `description` text NOT NULL,
  `testId` int(11) NOT NULL,
  `date_creation` bigint(20) NOT NULL,
  `date_modif` bigint(20) NOT NULL,
  `userId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Index pour les tables exportées
--

--
-- Index pour la table `tcomment`
--
ALTER TABLE `tcomment`
 ADD PRIMARY KEY (`id`), ADD UNIQUE KEY `testId` (`testId`);

--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `tcomment`
--
ALTER TABLE `tcomment`
ADD CONSTRAINT `commentCascade` FOREIGN KEY (`testId`) REFERENCES `test` (`id`) ON DELETE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
