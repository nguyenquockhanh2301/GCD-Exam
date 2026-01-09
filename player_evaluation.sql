-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 09, 2026 at 02:23 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `player_evaluation`
--

-- --------------------------------------------------------

--
-- Table structure for table `indexer`
--

CREATE TABLE `indexer` (
  `index_id` int(11) NOT NULL,
  `name` varchar(64) NOT NULL,
  `valueMin` float NOT NULL,
  `valueMax` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `indexer`
--

INSERT INTO `indexer` (`index_id`, `name`, `valueMin`, `valueMax`) VALUES
(1, 'speed', 10, 100),
(2, 'strength', 0, 10),
(3, 'accurate', 0, 1);

-- --------------------------------------------------------

--
-- Table structure for table `player`
--

CREATE TABLE `player` (
  `player_id` int(11) NOT NULL,
  `name` varchar(64) NOT NULL,
  `full_name` varchar(128) NOT NULL,
  `age` varchar(10) NOT NULL,
  `index_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `player`
--

INSERT INTO `player` (`player_id`, `name`, `full_name`, `age`, `index_id`) VALUES
(1, 'Shiro', 'Shiro', '13', 1),
(2, 'John', 'John', '44', 2),
(3, 'Thomas', 'Thomas', '24', 3),
(4, 'Bob', 'Bob', '21', 1),
(5, 'Smoker', 'Smoker', '25', 1),
(6, 'Arthur', 'Arthur', '46', 3),
(7, 'Hibiki', 'Hibiki', '21', 1),
(8, 'Ken', 'Ken', '33', 1),
(9, 'Mira', 'Mira', '34', 1),
(10, 'Mea', 'Mea', '45', 1),
(11, 'Shawn', 'Shawn', '35', 1);

-- --------------------------------------------------------

--
-- Table structure for table `player_index`
--

CREATE TABLE `player_index` (
  `id` int(11) NOT NULL,
  `player_id` int(11) NOT NULL,
  `index_id` int(11) NOT NULL,
  `value` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `player_index`
--

INSERT INTO `player_index` (`id`, `player_id`, `index_id`, `value`) VALUES
(1, 1, 1, 40),
(2, 2, 2, 3),
(3, 3, 3, 1),
(4, 4, 1, 12),
(5, 5, 1, 33),
(6, 6, 3, 1),
(7, 7, 1, 23),
(8, 8, 1, 85),
(9, 9, 1, 42),
(10, 10, 1, 11),
(11, 11, 1, 42);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `indexer`
--
ALTER TABLE `indexer`
  ADD PRIMARY KEY (`index_id`);

--
-- Indexes for table `player`
--
ALTER TABLE `player`
  ADD PRIMARY KEY (`player_id`),
  ADD KEY `index_id` (`index_id`);

--
-- Indexes for table `player_index`
--
ALTER TABLE `player_index`
  ADD PRIMARY KEY (`id`),
  ADD KEY `player_id` (`player_id`),
  ADD KEY `index_id` (`index_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `indexer`
--
ALTER TABLE `indexer`
  MODIFY `index_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `player`
--
ALTER TABLE `player`
  MODIFY `player_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `player_index`
--
ALTER TABLE `player_index`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `player`
--
ALTER TABLE `player`
  ADD CONSTRAINT `player_ibfk_1` FOREIGN KEY (`index_id`) REFERENCES `indexer` (`index_id`);

--
-- Constraints for table `player_index`
--
ALTER TABLE `player_index`
  ADD CONSTRAINT `player_index_ibfk_1` FOREIGN KEY (`player_id`) REFERENCES `player` (`player_id`),
  ADD CONSTRAINT `player_index_ibfk_2` FOREIGN KEY (`index_id`) REFERENCES `indexer` (`index_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
