-- phpMyAdmin SQL Dump
-- version 4.2.12deb1
-- http://www.phpmyadmin.net
--
-- Client :  localhost
-- Généré le :  Jeu 11 Février 2016 à 12:21
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
-- Structure de la table `catstatistic`
--

CREATE TABLE IF NOT EXISTS `catstatistic` (
  `id` int(11) NOT NULL,
  `suiteId` int(11) NOT NULL,
  `categoryId` int(11) NOT NULL,
  `running` int(11) NOT NULL DEFAULT '0',
  `success` int(11) NOT NULL DEFAULT '0',
  `failure` int(11) NOT NULL DEFAULT '0',
  `error` int(11) NOT NULL DEFAULT '0',
  `errorCrash` int(11) NOT NULL DEFAULT '0',
  `errorTimeout` int(11) NOT NULL DEFAULT '0',
  `errorEx` int(11) NOT NULL DEFAULT '0',
  `skipped` int(11) NOT NULL DEFAULT '0',
  `skippedDep` int(11) NOT NULL DEFAULT '0',
  `skippedPro` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `clcategory`
--

CREATE TABLE IF NOT EXISTS `clcategory` (
  `id` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  `suitenames` varchar(128) NOT NULL,
  `catDefault` tinyint(4) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `message`
--

CREATE TABLE IF NOT EXISTS `message` (
  `id` int(11) NOT NULL,
  `type` varchar(50) NOT NULL,
  `message` varchar(256) DEFAULT NULL,
  `stacktrace` text,
  `outputlog` text,
  `testId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `suite`
--

CREATE TABLE IF NOT EXISTS `suite` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `firmware` varchar(50) NOT NULL,
  `iptvkit` varchar(50) DEFAULT NULL,
  `comment` text,
  `log` tinyint(4) NOT NULL DEFAULT '0',
  `time` bigint(20) NOT NULL,
  `date` bigint(20) NOT NULL,
  `groupId` int(11) NOT NULL,
  `userId` int(11) NOT NULL DEFAULT '-1',
  `readonly` tinyint(4) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `suitegroup`
--

CREATE TABLE IF NOT EXISTS `suitegroup` (
  `id` int(11) NOT NULL,
  `stb` varchar(20) NOT NULL,
  `name` varchar(20) NOT NULL,
  `prefix` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `tclass`
--

CREATE TABLE IF NOT EXISTS `tclass` (
  `id` int(11) NOT NULL,
  `name` varchar(128) NOT NULL,
  `categoryId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `test`
--

CREATE TABLE IF NOT EXISTS `test` (
  `id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `status` varchar(20) NOT NULL,
  `time` bigint(20) NOT NULL,
  `suiteId` int(11) NOT NULL,
  `tclassId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `tuser`
--

CREATE TABLE IF NOT EXISTS `tuser` (
  `id` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `admin` tinyint(4) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Index pour les tables exportées
--

--
-- Index pour la table `clcategory`
--
ALTER TABLE `clcategory`
 ADD PRIMARY KEY (`id`), ADD UNIQUE KEY `name` (`name`);

--
-- Index pour la table `message`
--
ALTER TABLE `message`
 ADD PRIMARY KEY (`id`), ADD KEY `testId` (`testId`);

--
-- Index pour la table `suite`
--
ALTER TABLE `suite`
 ADD PRIMARY KEY (`id`), ADD UNIQUE KEY `name` (`name`), ADD KEY `groupId` (`groupId`), ADD KEY `userId` (`userId`);

--
-- Index pour la table `suitegroup`
--
ALTER TABLE `suitegroup`
 ADD PRIMARY KEY (`id`), ADD UNIQUE KEY `name` (`name`);

--
-- Index pour la table `tclass`
--
ALTER TABLE `tclass`
 ADD PRIMARY KEY (`id`), ADD UNIQUE KEY `name` (`name`);

--
-- Index pour la table `test`
--
ALTER TABLE `test`
 ADD PRIMARY KEY (`id`), ADD KEY `suiteId` (`suiteId`), ADD KEY `tclassId` (`tclassId`);

--
-- Index pour la table `tuser`
--
ALTER TABLE `tuser`
 ADD PRIMARY KEY (`id`), ADD UNIQUE KEY `name` (`name`);

--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `message`
--
ALTER TABLE `message`
ADD CONSTRAINT `messageCascade` FOREIGN KEY (`testId`) REFERENCES `test` (`id`) ON DELETE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
