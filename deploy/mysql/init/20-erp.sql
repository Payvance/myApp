-- phpMyAdmin SQL Dump
-- version 5.2.1deb3
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Feb 09, 2026 at 10:37 AM
-- Server version: 8.0.45-0ubuntu0.24.04.1
-- PHP Version: 8.3.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `erp`
--

-- --------------------------------------------------------

--
-- Table structure for table `sync_state`
--

CREATE TABLE `sync_state` (
  `id` bigint NOT NULL,
  `company_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `last_alter_id` bigint DEFAULT NULL,
  `last_sync_time` datetime(6) DEFAULT NULL,
  `tenant_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_batch_allocations`
--

CREATE TABLE `tally_batch_allocations` (
  `id` bigint NOT NULL,
  `actual_qty` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `batch_discount` double DEFAULT NULL,
  `batch_id` bigint DEFAULT NULL,
  `batch_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `billed_qty` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `godown_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `indent_no` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `order_no` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rate` double DEFAULT NULL,
  `tracking_number` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `inventory_entry_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_companies`
--

CREATE TABLE `tally_companies` (
  `id` bigint NOT NULL,
  `books_from` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `financial_year` int DEFAULT NULL,
  `gstin` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guid` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tenant_id` bigint NOT NULL,
  `timezone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `financial_year_from` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `financial_year_to` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pan` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_configuration`
--

CREATE TABLE `tally_configuration` (
  `id` bigint NOT NULL,
  `tenant_id` bigint NOT NULL,
  `host` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `port` int NOT NULL,
  `license_expiry_date` date NOT NULL,
  `license_serial_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `license_email` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_cost_centres`
--

CREATE TABLE `tally_cost_centres` (
  `id` bigint NOT NULL,
  `category_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `company_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_reserved` bit(1) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tenant_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_godowns`
--

CREATE TABLE `tally_godowns` (
  `id` bigint NOT NULL,
  `company_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_reserved` bit(1) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_guid` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tenant_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_groups`
--

CREATE TABLE `tally_groups` (
  `id` bigint NOT NULL,
  `company_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_reserved` bit(1) DEFAULT NULL,
  `is_revenue` bit(1) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_guid` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `primary_group` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sort_position` int DEFAULT NULL,
  `tenant_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_inventory_entries`
--

CREATE TABLE `tally_inventory_entries` (
  `id` bigint NOT NULL,
  `amount` decimal(19,4) DEFAULT NULL,
  `billed_qty` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rate` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `stock_item_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `voucher_id` bigint DEFAULT NULL,
  `actual_qty` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ledger_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `actual_qty_num` decimal(18,4) DEFAULT NULL,
  `gst_rate` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_ledgers`
--

CREATE TABLE `tally_ledgers` (
  `id` bigint NOT NULL,
  `address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `alias` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `company_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `group_guid` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gstin` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_billwise` bit(1) DEFAULT NULL,
  `is_party` bit(1) DEFAULT NULL,
  `mobile` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `opening_balance` decimal(38,2) DEFAULT NULL,
  `pan` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pincode` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tenant_id` bigint NOT NULL,
  `group_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_revenue` bit(1) DEFAULT NULL,
  `closing_balance` decimal(38,2) DEFAULT NULL,
  `email_cc` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fax` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gst_party_contact` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bank_account_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bank_account_number` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bank_bsr_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bank_branch_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cess_rate` decimal(38,2) DEFAULT NULL,
  `cgst_rate` decimal(38,2) DEFAULT NULL,
  `city` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contact_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `country_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gst_dealer_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hsn_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hsn_description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ifsc_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `igst_rate` decimal(38,2) DEFAULT NULL,
  `is_cost_center` bit(1) DEFAULT NULL,
  `narration` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `notes` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone_number` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price_level` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `prior_state_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sgst_rate` decimal(38,2) DEFAULT NULL,
  `state_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `swift_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `website` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_ledger_entries`
--

CREATE TABLE `tally_ledger_entries` (
  `id` bigint NOT NULL,
  `amount` decimal(19,4) DEFAULT NULL,
  `is_debit` bit(1) DEFAULT NULL,
  `ledger_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `voucher_id` bigint DEFAULT NULL,
  `is_party_ledger` bit(1) DEFAULT NULL,
  `method_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cgst_amount` decimal(19,4) DEFAULT NULL,
  `cgst_rate` double DEFAULT NULL,
  `gst_class` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gst_override` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `igst_amount` decimal(19,4) DEFAULT NULL,
  `igst_rate` double DEFAULT NULL,
  `sgst_amount` decimal(19,4) DEFAULT NULL,
  `sgst_rate` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_masters`
--

CREATE TABLE `tally_masters` (
  `id` bigint NOT NULL,
  `guid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tenant_id` bigint NOT NULL,
  `type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_stock_categories`
--

CREATE TABLE `tally_stock_categories` (
  `id` bigint NOT NULL,
  `company_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_guid` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tenant_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_stock_groups`
--

CREATE TABLE `tally_stock_groups` (
  `id` bigint NOT NULL,
  `company_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_reserved` bit(1) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_guid` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parent_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tenant_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_stock_items`
--

CREATE TABLE `tally_stock_items` (
  `tenant_id` bigint NOT NULL,
  `id` bigint NOT NULL,
  `company_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `conversion_factor` double DEFAULT NULL,
  `gst_hsn_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gst_rate` double DEFAULT NULL,
  `guid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_batchwise` bit(1) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `opening_quantity` double DEFAULT NULL,
  `opening_rate` double DEFAULT NULL,
  `opening_value` decimal(19,4) DEFAULT NULL,
  `stock_group_guid` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unit_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `alias` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `alternate_unit` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `category_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gst_taxability` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_godown_tracking` bit(1) DEFAULT NULL,
  `is_reserved` bit(1) DEFAULT NULL,
  `stock_group_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cess_rate` double DEFAULT NULL,
  `cgst_rate` double DEFAULT NULL,
  `closing_quantity` double DEFAULT NULL,
  `closing_rate` double DEFAULT NULL,
  `closing_value` decimal(19,4) DEFAULT NULL,
  `costing_method` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `igst_rate` double DEFAULT NULL,
  `inward_quantity` double DEFAULT NULL,
  `inward_value` decimal(19,4) DEFAULT NULL,
  `last_purchase_date` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_purchase_party` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_purchase_price` decimal(19,4) DEFAULT NULL,
  `last_purchase_quantity` double DEFAULT NULL,
  `last_sale_date` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_sale_party` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_sale_price` decimal(19,4) DEFAULT NULL,
  `last_sale_quantity` double DEFAULT NULL,
  `outward_quantity` double DEFAULT NULL,
  `outward_value` decimal(19,4) DEFAULT NULL,
  `sgst_rate` double DEFAULT NULL,
  `valuation_method` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_sync_settings`
--

CREATE TABLE `tally_sync_settings` (
  `id` bigint NOT NULL,
  `chunk_size` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `deleted_voucher_sync_minutes` int DEFAULT NULL,
  `enable_chunking` bit(1) NOT NULL,
  `enable_deleted_voucher_sync` bit(1) NOT NULL,
  `enable_master_size_chunk` bit(1) NOT NULL,
  `enable_notifications` bit(1) NOT NULL,
  `enable_proxy` bit(1) NOT NULL,
  `no_admin_access` bit(1) NOT NULL,
  `profit_loss_sync_hours` int NOT NULL,
  `proxy_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sync_interval_minutes` int NOT NULL,
  `tally_retry_attempts` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `tenant_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_tax_units`
--

CREATE TABLE `tally_tax_units` (
  `id` bigint NOT NULL,
  `company_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `registration_number` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tax_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tenant_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_units`
--

CREATE TABLE `tally_units` (
  `id` bigint NOT NULL,
  `company_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `conversion` decimal(19,4) DEFAULT NULL,
  `decimal_places` int DEFAULT NULL,
  `first_unit` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_compound` bit(1) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `second_unit` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `symbol` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tenant_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_vouchers`
--

CREATE TABLE `tally_vouchers` (
  `id` bigint NOT NULL,
  `amount` decimal(19,4) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `guid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `narration` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tenant_id` bigint NOT NULL,
  `voucher_number` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `voucher_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `alter_id` bigint DEFAULT NULL,
  `company_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_invoice` bit(1) DEFAULT NULL,
  `master_id` bigint DEFAULT NULL,
  `party_ledger_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `consignee_address` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `consignee_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `delivery_notes` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `payment_terms` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ack_no` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `basic_buyer_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bill_place` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `buyer_address` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cmp_gst` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cmp_reg_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cmp_state` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dispatch_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dispatch_pin` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dispatch_place` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `dispatch_state` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gst_registration_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `irn` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `irn_ack_date` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `irn_qr_code` varchar(5000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `irp_source` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_eway_applicable` bit(1) DEFAULT NULL,
  `party_gst` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `party_mailing_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `party_pincode` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `place_of_supply` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ship_place` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `voucher_category` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_voucher_types`
--

CREATE TABLE `tally_voucher_types` (
  `id` bigint NOT NULL,
  `company_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `guid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_invoice` bit(1) DEFAULT NULL,
  `is_optional` bit(1) DEFAULT NULL,
  `is_reserved` bit(1) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `numbering_method` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tenant_id` bigint NOT NULL,
  `closing_balance` decimal(19,4) DEFAULT NULL,
  `parent` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_writeback_jobs`
--

CREATE TABLE `tally_writeback_jobs` (
  `id` bigint NOT NULL,
  `company_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `entity_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `entity_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `error_message` text COLLATE utf8mb4_unicode_ci,
  `payload` text COLLATE utf8mb4_unicode_ci,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tenant_id` bigint NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `sync_state`
--
ALTER TABLE `sync_state`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKg1ixyk3meifnxdf7i6v4935qt` (`tenant_id`,`company_id`);

--
-- Indexes for table `tally_batch_allocations`
--
ALTER TABLE `tally_batch_allocations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKi6glc591t6pa53uuj4f6iq96r` (`inventory_entry_id`);

--
-- Indexes for table `tally_companies`
--
ALTER TABLE `tally_companies`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKfcw6dmk9hcwf2ejob6agxmaxe` (`guid`);

--
-- Indexes for table `tally_configuration`
--
ALTER TABLE `tally_configuration`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tally_cost_centres`
--
ALTER TABLE `tally_cost_centres`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKb681jc0b87ufno1ua0vmyjsll` (`guid`);

--
-- Indexes for table `tally_godowns`
--
ALTER TABLE `tally_godowns`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKswllhdbs5q0mvnhv6soxrf0l7` (`guid`);

--
-- Indexes for table `tally_groups`
--
ALTER TABLE `tally_groups`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK8h4qiv9ra661j6aminx7co1cg` (`guid`),
  ADD KEY `idx_groups_name` (`name`);

--
-- Indexes for table `tally_inventory_entries`
--
ALTER TABLE `tally_inventory_entries`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_inventory_voucher` (`voucher_id`),
  ADD KEY `idx_inventory_stock_item` (`stock_item_name`),
  ADD KEY `idx_inventory_qty` (`voucher_id`,`stock_item_name`),
  ADD KEY `idx_inventory_qty_num` (`actual_qty_num`);

--
-- Indexes for table `tally_ledgers`
--
ALTER TABLE `tally_ledgers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKs0cyfsb4nq6horf3ggtevp8fh` (`guid`),
  ADD KEY `idx_ledgers_name` (`name`),
  ADD KEY `idx_ledgers_group` (`group_name`);

--
-- Indexes for table `tally_ledger_entries`
--
ALTER TABLE `tally_ledger_entries`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_ledger_entries_voucher` (`voucher_id`),
  ADD KEY `idx_ledger_entries_ledger` (`ledger_name`);

--
-- Indexes for table `tally_masters`
--
ALTER TABLE `tally_masters`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKd0ahag0jilffb3o892ys4e5s3` (`guid`);

--
-- Indexes for table `tally_stock_categories`
--
ALTER TABLE `tally_stock_categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKkr9w20brrjmoyjqdn9x95nrft` (`guid`);

--
-- Indexes for table `tally_stock_groups`
--
ALTER TABLE `tally_stock_groups`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKb7om7gyhdpnj8o3gqw1l9vb96` (`guid`);

--
-- Indexes for table `tally_stock_items`
--
ALTER TABLE `tally_stock_items`
  ADD PRIMARY KEY (`id`,`tenant_id`),
  ADD UNIQUE KEY `UK2flerq2kbghopoluf0lo3bh62` (`guid`),
  ADD KEY `idx_stock_items_name` (`name`),
  ADD KEY `idx_stock_items_group_category` (`stock_group_name`,`category_name`);

--
-- Indexes for table `tally_sync_settings`
--
ALTER TABLE `tally_sync_settings`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tally_tax_units`
--
ALTER TABLE `tally_tax_units`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK9y8g5dcsw2rf0i2bx908fy3k4` (`guid`);

--
-- Indexes for table `tally_units`
--
ALTER TABLE `tally_units`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKojy61songwcf1vngdnpb2m66g` (`guid`);

--
-- Indexes for table `tally_vouchers`
--
ALTER TABLE `tally_vouchers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKpdpaqxaax1lkjdabpq0h6ujao` (`guid`),
  ADD KEY `idx_vouchers_vouchertype` (`voucher_type`),
  ADD KEY `idx_vouchers_id` (`id`),
  ADD KEY `idx_vouchers_vouchertype_date` (`voucher_type`,`date`);

--
-- Indexes for table `tally_voucher_types`
--
ALTER TABLE `tally_voucher_types`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK95u3ks68ojfdf7mx8tgda6oqk` (`guid`);

--
-- Indexes for table `tally_writeback_jobs`
--
ALTER TABLE `tally_writeback_jobs`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `sync_state`
--
ALTER TABLE `sync_state`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_batch_allocations`
--
ALTER TABLE `tally_batch_allocations`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_companies`
--
ALTER TABLE `tally_companies`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_configuration`
--
ALTER TABLE `tally_configuration`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_cost_centres`
--
ALTER TABLE `tally_cost_centres`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_godowns`
--
ALTER TABLE `tally_godowns`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_groups`
--
ALTER TABLE `tally_groups`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_inventory_entries`
--
ALTER TABLE `tally_inventory_entries`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_ledgers`
--
ALTER TABLE `tally_ledgers`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_ledger_entries`
--
ALTER TABLE `tally_ledger_entries`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_masters`
--
ALTER TABLE `tally_masters`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_stock_categories`
--
ALTER TABLE `tally_stock_categories`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_stock_groups`
--
ALTER TABLE `tally_stock_groups`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_stock_items`
--
ALTER TABLE `tally_stock_items`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_sync_settings`
--
ALTER TABLE `tally_sync_settings`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_tax_units`
--
ALTER TABLE `tally_tax_units`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_units`
--
ALTER TABLE `tally_units`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_vouchers`
--
ALTER TABLE `tally_vouchers`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_voucher_types`
--
ALTER TABLE `tally_voucher_types`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_writeback_jobs`
--
ALTER TABLE `tally_writeback_jobs`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `tally_batch_allocations`
--
ALTER TABLE `tally_batch_allocations`
  ADD CONSTRAINT `FKi6glc591t6pa53uuj4f6iq96r` FOREIGN KEY (`inventory_entry_id`) REFERENCES `tally_inventory_entries` (`id`);

--
-- Constraints for table `tally_inventory_entries`
--
ALTER TABLE `tally_inventory_entries`
  ADD CONSTRAINT `FKovmf96d1lv0lqpvd9c3dyi4fs` FOREIGN KEY (`voucher_id`) REFERENCES `tally_vouchers` (`id`);

--
-- Constraints for table `tally_ledger_entries`
--
ALTER TABLE `tally_ledger_entries`
  ADD CONSTRAINT `FK4a0hf7o6ae2ilc92hx7cg1ctd` FOREIGN KEY (`voucher_id`) REFERENCES `tally_vouchers` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

