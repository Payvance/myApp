-- phpMyAdmin SQL Dump
-- version 5.2.1deb3
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Feb 19, 2026 at 06:21 AM
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
-- Database: `core_saas`
--

-- --------------------------------------------------------

--
-- Table structure for table `activation_keys`
--

CREATE TABLE `activation_keys` (
  `id` bigint UNSIGNED NOT NULL,
  `vendor_batch_id` bigint DEFAULT NULL,
  `activation_code_hash` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `plain_code_last4` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('UNUSED','ISSUED','REDEEMED','EXPIRED','REVOKED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'UNUSED',
  `issued_to_email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `issued_to_phone` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `redeemed_tenant_id` bigint UNSIGNED DEFAULT NULL,
  `redeemed_at` timestamp NULL DEFAULT NULL,
  `expires_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `addons`
--

CREATE TABLE `addons` (
  `id` bigint UNSIGNED NOT NULL,
  `code` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INR',
  `unit` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'unit',
  `unit_price` decimal(12,2) NOT NULL DEFAULT '0.00',
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `plan_id` bigint DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `audit_logs`
--

CREATE TABLE `audit_logs` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED DEFAULT NULL,
  `actor_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `actor_user_id` bigint UNSIGNED DEFAULT NULL,
  `action` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `entity_type` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `entity_id` bigint UNSIGNED DEFAULT NULL,
  `meta_json` json DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `bank_details`
--

CREATE TABLE `bank_details` (
  `id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `bank_name` varchar(255) DEFAULT NULL,
  `branch_name` varchar(255) DEFAULT NULL,
  `account_number` varchar(255) DEFAULT NULL,
  `ifsc_code` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `bank_transfer_requests`
--

CREATE TABLE `bank_transfer_requests` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED DEFAULT NULL,
  `wallet_id` bigint UNSIGNED DEFAULT NULL,
  `referals_count` int DEFAULT NULL,
  `amount` decimal(38,2) DEFAULT NULL,
  `payment_mode` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `utr_number` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `payer_bank` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `paid_amount` decimal(38,2) DEFAULT NULL,
  `paid_date` timestamp NULL DEFAULT NULL,
  `proof_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `verified_by_user_id` bigint UNSIGNED DEFAULT NULL,
  `verified_at` timestamp NULL DEFAULT NULL,
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ca`
--

CREATE TABLE `ca` (
  `id` bigint UNSIGNED NOT NULL,
  `user_id` bigint UNSIGNED DEFAULT NULL,
  `address_id` bigint DEFAULT NULL,
  `bank_details_id` bigint DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contact_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ca_reg_no` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `enrollment_year` int DEFAULT NULL,
  `icai_member_status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `practice_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `firm_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `icai_member_no` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `aadhar_no` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `rejection_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ca_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cin_no` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gst_no` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pan_no` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tan_no` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ca_tenants`
--

CREATE TABLE `ca_tenants` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `ca_id` bigint UNSIGNED NOT NULL,
  `is_view` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `companies_registry`
--

CREATE TABLE `companies_registry` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `display_name` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `tally_company_name` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `registered_mobile` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `company_access_otps`
--

CREATE TABLE `company_access_otps` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `tenant_company_id` bigint UNSIGNED NOT NULL,
  `requested_by_user_id` bigint UNSIGNED DEFAULT NULL,
  `otp_hash` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `expires_at` timestamp NULL DEFAULT NULL,
  `verified_at` timestamp NULL DEFAULT NULL,
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'created',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `company_config`
--

CREATE TABLE `company_config` (
  `id` bigint NOT NULL,
  `address` text,
  `company_name` varchar(150) NOT NULL,
  `email` varchar(120) DEFAULT NULL,
  `phone` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `config`
--

CREATE TABLE `config` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `config_key` varchar(50) NOT NULL,
  `status` varchar(20) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `config_value` varchar(100) NOT NULL,
  `value` varchar(60) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `connector_commands`
--

CREATE TABLE `connector_commands` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `tenant_company_id` bigint UNSIGNED NOT NULL,
  `connector_device_id` bigint UNSIGNED DEFAULT NULL,
  `command_type` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `payload_json` json NOT NULL,
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `idempotency_key` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `attempts` int UNSIGNED NOT NULL DEFAULT '0',
  `locked_at` timestamp NULL DEFAULT NULL,
  `locked_by_device_uid` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `result_json` json DEFAULT NULL,
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `connector_devices`
--

CREATE TABLE `connector_devices` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `device_uid` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `device_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `app_version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active',
  `last_seen_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `connector_keys`
--

CREATE TABLE `connector_keys` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `key_hash` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `revoked_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `connector_logs`
--

CREATE TABLE `connector_logs` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `device_id` bigint UNSIGNED DEFAULT NULL,
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'info',
  `code` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `details_json` json DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `connector_releases`
--

CREATE TABLE `connector_releases` (
  `id` bigint UNSIGNED NOT NULL,
  `version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `platform` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'windows',
  `download_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sha256` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_latest` tinyint(1) NOT NULL DEFAULT '0',
  `release_notes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `coupons`
--

CREATE TABLE `coupons` (
  `id` bigint UNSIGNED NOT NULL,
  `code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `discount_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `discount_percentage` float DEFAULT NULL,
  `discount_value` double DEFAULT NULL,
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INR',
  `valid_from` date DEFAULT NULL,
  `valid_to` date DEFAULT NULL,
  `max_uses` bigint DEFAULT NULL,
  `used_count` bigint DEFAULT NULL,
  `discription` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `coupon_redemptions`
--

CREATE TABLE `coupon_redemptions` (
  `id` bigint UNSIGNED NOT NULL,
  `coupon_id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `invoice_id` bigint UNSIGNED DEFAULT NULL,
  `discount_applied` decimal(12,2) NOT NULL DEFAULT '0.00',
  `redeemed_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `email_otp`
--

CREATE TABLE `email_otp` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `otp` varchar(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `events`
--

CREATE TABLE `events` (
  `id` bigint NOT NULL,
  `actor_user_id` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `entity_id` bigint DEFAULT NULL,
  `entity_type` varchar(255) DEFAULT NULL,
  `event_type` varchar(255) DEFAULT NULL,
  `payload` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `failed_jobs`
--

CREATE TABLE `failed_jobs` (
  `id` bigint UNSIGNED NOT NULL,
  `uuid` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `connection` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `queue` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `payload` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `exception` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `failed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `gst_rate`
--

CREATE TABLE `gst_rate` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `effective_date` date NOT NULL,
  `rate` double NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `integrations`
--

CREATE TABLE `integrations` (
  `id` bigint UNSIGNED NOT NULL,
  `code` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `requires_separate_connector` tinyint(1) NOT NULL DEFAULT '1',
  `release_cycle_days` int UNSIGNED NOT NULL DEFAULT '30',
  `config_json` json DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `invoices`
--

CREATE TABLE `invoices` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `subscription_id` bigint UNSIGNED DEFAULT NULL,
  `invoice_number` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `gateway` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `subtotal` decimal(12,2) NOT NULL DEFAULT '0.00',
  `discount_total` decimal(12,2) NOT NULL DEFAULT '0.00',
  `discount_by` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `total_payable` decimal(12,2) NOT NULL DEFAULT '0.00',
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INR',
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'unpaid',
  `paid_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `gateway_payment_id` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `invoice_items`
--

CREATE TABLE `invoice_items` (
  `id` bigint UNSIGNED NOT NULL,
  `invoice_id` bigint UNSIGNED NOT NULL,
  `item_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quantity` int UNSIGNED NOT NULL DEFAULT '1',
  `unit_price` decimal(12,2) NOT NULL DEFAULT '0.00',
  `line_total` decimal(12,2) NOT NULL DEFAULT '0.00',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `license_models`
--

CREATE TABLE `license_models` (
  `id` bigint UNSIGNED NOT NULL,
  `code` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INR',
  `model_json` json DEFAULT NULL,
  `validity_days` int UNSIGNED NOT NULL DEFAULT '365',
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `migrations`
--

CREATE TABLE `migrations` (
  `id` int UNSIGNED NOT NULL,
  `migration` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `batch` int NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `password_reset_tokens`
--

CREATE TABLE `password_reset_tokens` (
  `email` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `token` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

CREATE TABLE `payments` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `subscription_id` bigint UNSIGNED DEFAULT NULL,
  `gateway` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `gateway_payment_id` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending',
  `amount` decimal(12,2) NOT NULL DEFAULT '0.00',
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INR',
  `paid_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `gateway_order_id` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `invoice_id` bigint DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `payment_webhooks`
--

CREATE TABLE `payment_webhooks` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED DEFAULT NULL,
  `invoice_id` bigint UNSIGNED DEFAULT NULL,
  `gateway` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `event_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `signature_valid` tinyint(1) NOT NULL DEFAULT '0',
  `payload_json` json DEFAULT NULL,
  `received_at` timestamp NULL DEFAULT NULL,
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'received',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `personal_access_tokens`
--

CREATE TABLE `personal_access_tokens` (
  `id` bigint UNSIGNED NOT NULL,
  `tokenable_type` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `tokenable_id` bigint UNSIGNED NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `abilities` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `last_used_at` timestamp NULL DEFAULT NULL,
  `expires_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `user_id` bigint UNSIGNED DEFAULT NULL,
  `tenant_id` bigint UNSIGNED DEFAULT NULL,
  `token_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `plan`
--

CREATE TABLE `plan` (
  `id` bigint UNSIGNED NOT NULL,
  `code` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_active` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1',
  `is_seprate_db` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '0',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `tenant_id` bigint DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `plan_limitations`
--

CREATE TABLE `plan_limitations` (
  `id` bigint UNSIGNED NOT NULL,
  `plan_id` bigint UNSIGNED NOT NULL,
  `allowed_user_count` int UNSIGNED NOT NULL DEFAULT '1',
  `allowed_company_count` int UNSIGNED NOT NULL DEFAULT '1',
  `allowed_user_count_till` int UNSIGNED NOT NULL DEFAULT '0',
  `allowed_company_count_till` int UNSIGNED NOT NULL DEFAULT '0',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `plan_prices`
--

CREATE TABLE `plan_prices` (
  `id` bigint UNSIGNED NOT NULL,
  `plan_id` bigint UNSIGNED NOT NULL,
  `billing_period` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INR',
  `amount` decimal(12,2) NOT NULL DEFAULT '0.00',
  `is_active` tinyint NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `period_value` int NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `referrals`
--

CREATE TABLE `referrals` (
  `id` bigint UNSIGNED NOT NULL,
  `program_id` bigint UNSIGNED NOT NULL,
  `bank_transfer_id` bigint DEFAULT NULL,
  `referral_code_id` bigint UNSIGNED NOT NULL,
  `referrer_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `referrer_id` bigint UNSIGNED NOT NULL,
  `referred_tenant_id` bigint UNSIGNED NOT NULL,
  `rewarded_amount` decimal(38,2) NOT NULL,
  `qualified_at` timestamp NULL DEFAULT NULL,
  `rewarded_at` timestamp NULL DEFAULT NULL,
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `referrer_tenant_id` bigint NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `referral_codes`
--

CREATE TABLE `referral_codes` (
  `id` bigint UNSIGNED NOT NULL,
  `program_id` bigint UNSIGNED NOT NULL,
  `owner_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `owner_id` bigint UNSIGNED NOT NULL,
  `code` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `max_uses` int UNSIGNED NOT NULL DEFAULT '0',
  `used_count` int UNSIGNED NOT NULL DEFAULT '0',
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `referral_programs`
--

CREATE TABLE `referral_programs` (
  `id` bigint UNSIGNED NOT NULL,
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `owner_type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `reward_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `reward_value` double DEFAULT NULL,
  `reward_percentage` float DEFAULT NULL,
  `reward_trigger` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `max_per_referrer` double DEFAULT NULL,
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `role_id` bigint NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `referral_programs`
--

INSERT INTO `referral_programs` (`id`, `code`, `owner_type`, `name`, `reward_type`, `reward_value`, `reward_percentage`, `reward_trigger`, `max_per_referrer`, `status`, `created_at`, `updated_at`, `role_id`) VALUES
(1, 'CA_REF_2025', 'CA', 'CA Referral Program', 'flat', 250, NULL, 'on_payment', NULL, 'active', '2025-12-23 00:13:46', '2025-12-23 00:13:46', 5),
(2, 'TA_REF_2025', 'TENANT_ADMIN', 'TA Referral Program', 'flat', 250, NULL, 'on_payment', NULL, 'ACTIVE', '2026-01-09 11:39:08', '2026-01-09 11:39:24', 2),
(3, '0917', 'CA', 'Test', 'FLAT', 120, NULL, NULL, 0, 'ACTIVE', '2026-01-09 16:24:53', '2026-01-09 16:24:53', 0),
(4, 'gy', 'Tenent', '4465', 'PERCENTAGE', NULL, 54, NULL, 0, 'ACTIVE', '2026-01-09 16:25:19', '2026-01-09 16:25:19', 0),
(5, 'sam09', 'Tenent', 'testing09', 'PERCENTAGE', NULL, 10, NULL, 0, 'INACTIVE', '2026-01-10 10:38:01', '2026-01-13 17:28:18', 0),
(6, 'dqw', 'CA', 'few`', 'FLAT', 9, NULL, NULL, 0, 'ACTIVE', '2026-01-10 10:39:04', '2026-01-10 10:39:04', 0),
(7, '88', 'CA', '8', 'PERCENTAGE', NULL, 8, NULL, 0, 'ACTIVE', '2026-01-12 11:24:30', '2026-01-12 11:24:30', 0);

-- --------------------------------------------------------

--
-- Table structure for table `role`
--

CREATE TABLE `role` (
  `id` bigint UNSIGNED NOT NULL,
  `code` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `scope` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `role`
--

INSERT INTO `role` (`id`, `code`, `name`, `scope`, `created_at`, `updated_at`) VALUES
(1, 'SUPER_ADMIN', 'Super Admin', 'platform', '2025-12-23 00:13:46', '2025-12-23 00:13:46'),
(2, 'TENANT_ADMIN', 'Tenant Admin', 'tenant', '2025-12-23 00:13:46', '2025-12-23 00:13:46'),
(3, 'TENANT_USER', 'Tenant User', 'tenant', '2025-12-23 00:13:46', '2025-12-23 00:13:46'),
(4, 'VENDOR', 'Vendor', 'platform', '2025-12-23 00:13:46', '2025-12-23 00:13:46'),
(5, 'CA', 'Chartered Accountant', 'platform', '2025-12-23 00:13:46', '2025-12-23 00:13:46');

-- --------------------------------------------------------

--
-- Table structure for table `subscriptions`
--

CREATE TABLE `subscriptions` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `plan_id` bigint UNSIGNED NOT NULL,
  `plan_price_id` bigint UNSIGNED DEFAULT NULL,
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active',
  `start_at` timestamp NULL DEFAULT NULL,
  `current_period_end` timestamp NULL DEFAULT NULL,
  `cancel_at_period_end` tinyint(1) NOT NULL DEFAULT '0',
  `cancelled_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `subscription_addons`
--

CREATE TABLE `subscription_addons` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `subscription_id` bigint UNSIGNED NOT NULL,
  `addon_id` bigint UNSIGNED NOT NULL,
  `quantity` int UNSIGNED NOT NULL DEFAULT '1',
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active',
  `effective_from` timestamp NULL DEFAULT NULL,
  `effective_to` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `subscription_events`
--

CREATE TABLE `subscription_events` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `subscription_id` bigint UNSIGNED NOT NULL,
  `event_type` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `from_plan_id` bigint UNSIGNED DEFAULT NULL,
  `to_plan_id` bigint UNSIGNED DEFAULT NULL,
  `payload_json` json DEFAULT NULL,
  `received_at` timestamp NULL DEFAULT NULL,
  `processed_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_inventory_entries`
--

CREATE TABLE `tally_inventory_entries` (
  `id` bigint NOT NULL,
  `amount` double DEFAULT NULL,
  `billed_qty` varchar(255) DEFAULT NULL,
  `rate` varchar(255) DEFAULT NULL,
  `stock_item_name` varchar(255) DEFAULT NULL,
  `voucher_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_ledger_entries`
--

CREATE TABLE `tally_ledger_entries` (
  `id` bigint NOT NULL,
  `amount` double DEFAULT NULL,
  `is_debit` bit(1) DEFAULT NULL,
  `ledger_name` varchar(255) DEFAULT NULL,
  `voucher_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_license_validations`
--

CREATE TABLE `tally_license_validations` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `tenant_company_id` bigint UNSIGNED NOT NULL,
  `tally_license_key_hash` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `validated_at` timestamp NULL DEFAULT NULL,
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_masters`
--

CREATE TABLE `tally_masters` (
  `id` bigint NOT NULL,
  `guid` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `parent` varchar(255) DEFAULT NULL,
  `tenant_id` bigint NOT NULL,
  `type` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_master_map`
--

CREATE TABLE `tally_master_map` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `tenant_company_id` bigint UNSIGNED NOT NULL,
  `master_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `cloud_code` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `tally_name` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `tally_guid` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active',
  `last_synced_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tally_vouchers`
--

CREATE TABLE `tally_vouchers` (
  `id` bigint NOT NULL,
  `amount` double DEFAULT NULL,
  `date` varchar(255) DEFAULT NULL,
  `guid` varchar(255) NOT NULL,
  `narration` varchar(1000) DEFAULT NULL,
  `tenant_id` bigint NOT NULL,
  `voucher_number` varchar(255) DEFAULT NULL,
  `voucher_type` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tenants`
--

CREATE TABLE `tenants` (
  `id` bigint UNSIGNED NOT NULL,
  `name` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `trial_start_at` timestamp NULL DEFAULT NULL,
  `trial_end_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tenant_activations`
--

CREATE TABLE `tenant_activations` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `license_model_id` bigint UNSIGNED DEFAULT NULL,
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `vendor_batch_id` bigint UNSIGNED DEFAULT NULL,
  `referral_id` bigint UNSIGNED DEFAULT NULL,
  `activation_code_hash` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `activation_price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INR',
  `activated_at` timestamp NULL DEFAULT NULL,
  `expires_at` timestamp NULL DEFAULT NULL,
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tenant_db_configs`
--

CREATE TABLE `tenant_db_configs` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `mode` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'shared',
  `db_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `db_host` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `db_port` int UNSIGNED DEFAULT NULL,
  `db_user` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `db_password_enc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tenant_integrations`
--

CREATE TABLE `tenant_integrations` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `integration_id` bigint UNSIGNED NOT NULL,
  `status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `source` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tenant_settings`
--

CREATE TABLE `tenant_settings` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `timezone` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `currency` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `max_companies` int UNSIGNED NOT NULL DEFAULT '1',
  `trial_days` int UNSIGNED NOT NULL DEFAULT '14',
  `extended_trial_days` int UNSIGNED NOT NULL DEFAULT '0',
  `ads_unlock_enabled` tinyint(1) NOT NULL DEFAULT '0',
  `ads_unlock_days` int UNSIGNED NOT NULL DEFAULT '0',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tenant_usage`
--

CREATE TABLE `tenant_usage` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `active_users_count` int UNSIGNED NOT NULL DEFAULT '0',
  `companies_count` int UNSIGNED NOT NULL DEFAULT '0',
  `last_sync_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `last_synced_at` datetime(6) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tenant_user_role`
--

CREATE TABLE `tenant_user_role` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `role_id` bigint UNSIGNED NOT NULL,
  `user_id` bigint UNSIGNED NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint UNSIGNED NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `remember_token` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email_verified_at` timestamp NULL DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `is_superadmin` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `rejection_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mobile_app_version` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mobile_device_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mobile_device_model` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mobile_fcm_token` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mobile_os_version` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mobile_platform` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `desktop_device_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user_addresses`
--

CREATE TABLE `user_addresses` (
  `id` bigint UNSIGNED NOT NULL,
  `user_id` bigint UNSIGNED NOT NULL,
  `house_building_no` varchar(255) DEFAULT NULL,
  `house_building_name` varchar(255) DEFAULT NULL,
  `road_area_place` varchar(255) DEFAULT NULL,
  `landmark` varchar(255) DEFAULT NULL,
  `village` varchar(255) DEFAULT NULL,
  `taluka` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `district` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `pincode` varchar(255) DEFAULT NULL,
  `post_office` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `vendor`
--

CREATE TABLE `vendor` (
  `id` bigint UNSIGNED NOT NULL,
  `user_id` bigint UNSIGNED DEFAULT NULL,
  `address_id` bigint DEFAULT NULL,
  `bank_details_id` bigint DEFAULT NULL,
  `vendor_discount_id` bigint DEFAULT '1',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contact_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `vendor_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `experience_years` int DEFAULT NULL,
  `gst_no` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cin_no` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `pan_no` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tan_no` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `aadhar_no` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `rejection_remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `vendors`
--

CREATE TABLE `vendors` (
  `id` bigint UNSIGNED NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `vendor_activation_batches`
--

CREATE TABLE `vendor_activation_batches` (
  `id` bigint UNSIGNED NOT NULL,
  `vendor_id` bigint UNSIGNED NOT NULL,
  `license_model_id` bigint UNSIGNED NOT NULL,
  `vendor_discount_id` int DEFAULT NULL,
  `total_activations` int UNSIGNED NOT NULL DEFAULT '0',
  `used_activations` int UNSIGNED NOT NULL DEFAULT '0',
  `cost_price` decimal(12,2) NOT NULL DEFAULT '0.00',
  `resale_price` decimal(12,2) NOT NULL DEFAULT '0.00',
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INR',
  `status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active',
  `issued_at` timestamp NULL DEFAULT NULL,
  `issued_by_user_id` bigint UNSIGNED DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `plan_id` bigint NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `vendor_discount`
--

CREATE TABLE `vendor_discount` (
  `id` bigint NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `value` double DEFAULT NULL,
  `effective_date` date DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `vendor_payment_upload`
--

CREATE TABLE `vendor_payment_upload` (
  `id` bigint UNSIGNED NOT NULL,
  `batch_id` bigint UNSIGNED NOT NULL,
  `payment_mode` varchar(50) NOT NULL,
  `payment_date` date DEFAULT NULL,
  `UTR_trn_no` varchar(100) NOT NULL,
  `remark` text,
  `image_upload` longblob,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Table structure for table `vendor_tenants`
--

CREATE TABLE `vendor_tenants` (
  `id` bigint UNSIGNED NOT NULL,
  `tenant_id` bigint UNSIGNED NOT NULL,
  `vendor_id` bigint UNSIGNED NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `wallets`
--

CREATE TABLE `wallets` (
  `id` bigint UNSIGNED NOT NULL,
  `owner_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `owner_id` bigint UNSIGNED NOT NULL,
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INR',
  `balance` double NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `wallet_transactions`
--

CREATE TABLE `wallet_transactions` (
  `id` bigint UNSIGNED NOT NULL,
  `wallet_id` bigint UNSIGNED NOT NULL,
  `txn_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `amount` decimal(14,2) NOT NULL DEFAULT '0.00',
  `currency` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INR',
  `reference_type` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reference_id` bigint UNSIGNED DEFAULT NULL,
  `remarks` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `activation_keys`
--
ALTER TABLE `activation_keys`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `activation_keys_activation_code_hash_unique` (`activation_code_hash`),
  ADD KEY `idx_activation_keys_batch` (`vendor_batch_id`),
  ADD KEY `idx_activation_keys_status` (`status`),
  ADD KEY `activation_keys_redeemed_tenant_id_foreign` (`redeemed_tenant_id`);

--
-- Indexes for table `addons`
--
ALTER TABLE `addons`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `addons_code_unique` (`code`),
  ADD KEY `addons_status_index` (`status`),
  ADD KEY `FKgkoy4o3bsvrdn1j3tqavgfjbu` (`plan_id`);

--
-- Indexes for table `audit_logs`
--
ALTER TABLE `audit_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `audit_logs_tenant_id_index` (`tenant_id`),
  ADD KEY `audit_logs_actor_type_index` (`actor_type`),
  ADD KEY `audit_logs_actor_user_id_index` (`actor_user_id`),
  ADD KEY `audit_logs_action_index` (`action`),
  ADD KEY `audit_logs_entity_type_index` (`entity_type`),
  ADD KEY `audit_logs_entity_id_index` (`entity_id`);

--
-- Indexes for table `bank_details`
--
ALTER TABLE `bank_details`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_user_id` (`user_id`);

--
-- Indexes for table `bank_transfer_requests`
--
ALTER TABLE `bank_transfer_requests`
  ADD PRIMARY KEY (`id`),
  ADD KEY `bank_transfer_requests_tenant_id_index` (`tenant_id`),
  ADD KEY `bank_transfer_requests_wallet_id_index` (`wallet_id`),
  ADD KEY `bank_transfer_requests_utr_number_index` (`utr_number`),
  ADD KEY `bank_transfer_requests_verified_by_user_id_index` (`verified_by_user_id`),
  ADD KEY `bank_transfer_requests_status_index` (`status`);

--
-- Indexes for table `ca`
--
ALTER TABLE `ca`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK7tp80mrqolwqonechr6sr14bv` (`user_id`),
  ADD KEY `ca_user_id_index` (`user_id`),
  ADD KEY `ca_email_index` (`email`),
  ADD KEY `ca_phone_index` (`phone`),
  ADD KEY `ca_status_index` (`status`);

--
-- Indexes for table `ca_tenants`
--
ALTER TABLE `ca_tenants`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_ca_tenant` (`tenant_id`,`ca_id`),
  ADD KEY `ca_tenants_tenant_id_index` (`tenant_id`),
  ADD KEY `ca_tenants_ca_id_index` (`ca_id`);

--
-- Indexes for table `companies_registry`
--
ALTER TABLE `companies_registry`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_tenant_tally_company` (`tenant_id`,`tally_company_name`),
  ADD KEY `companies_registry_tenant_id_index` (`tenant_id`),
  ADD KEY `companies_registry_status_index` (`status`);

--
-- Indexes for table `company_access_otps`
--
ALTER TABLE `company_access_otps`
  ADD PRIMARY KEY (`id`),
  ADD KEY `company_access_otps_tenant_id_index` (`tenant_id`),
  ADD KEY `company_access_otps_tenant_company_id_index` (`tenant_company_id`),
  ADD KEY `company_access_otps_requested_by_user_id_index` (`requested_by_user_id`),
  ADD KEY `company_access_otps_otp_hash_index` (`otp_hash`),
  ADD KEY `company_access_otps_status_index` (`status`);

--
-- Indexes for table `company_config`
--
ALTER TABLE `company_config`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `config`
--
ALTER TABLE `config`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `connector_commands`
--
ALTER TABLE `connector_commands`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_cmd_idempotency` (`tenant_company_id`,`idempotency_key`),
  ADD KEY `connector_commands_tenant_id_index` (`tenant_id`),
  ADD KEY `connector_commands_tenant_company_id_index` (`tenant_company_id`),
  ADD KEY `connector_commands_connector_device_id_index` (`connector_device_id`),
  ADD KEY `connector_commands_command_type_index` (`command_type`),
  ADD KEY `connector_commands_status_index` (`status`),
  ADD KEY `connector_commands_idempotency_key_index` (`idempotency_key`);

--
-- Indexes for table `connector_devices`
--
ALTER TABLE `connector_devices`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `connector_devices_device_uid_unique` (`device_uid`),
  ADD KEY `connector_devices_tenant_id_index` (`tenant_id`),
  ADD KEY `connector_devices_status_index` (`status`);

--
-- Indexes for table `connector_keys`
--
ALTER TABLE `connector_keys`
  ADD PRIMARY KEY (`id`),
  ADD KEY `connector_keys_tenant_id_index` (`tenant_id`),
  ADD KEY `connector_keys_key_hash_index` (`key_hash`);

--
-- Indexes for table `connector_logs`
--
ALTER TABLE `connector_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `connector_logs_tenant_id_index` (`tenant_id`),
  ADD KEY `connector_logs_device_id_index` (`device_id`);

--
-- Indexes for table `connector_releases`
--
ALTER TABLE `connector_releases`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `connector_releases_version_unique` (`version`),
  ADD KEY `connector_releases_is_latest_index` (`is_latest`);

--
-- Indexes for table `coupons`
--
ALTER TABLE `coupons`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `coupons_code_unique` (`code`),
  ADD KEY `coupons_discount_type_index` (`discount_type`),
  ADD KEY `coupons_status_index` (`status`);

--
-- Indexes for table `coupon_redemptions`
--
ALTER TABLE `coupon_redemptions`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_coupon_redemption` (`coupon_id`,`tenant_id`,`invoice_id`),
  ADD KEY `coupon_redemptions_coupon_id_index` (`coupon_id`),
  ADD KEY `coupon_redemptions_tenant_id_index` (`tenant_id`),
  ADD KEY `coupon_redemptions_invoice_id_index` (`invoice_id`);

--
-- Indexes for table `email_otp`
--
ALTER TABLE `email_otp`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKagpgp27lulkh46wjbkbpc7cfs` (`email`);

--
-- Indexes for table `events`
--
ALTER TABLE `events`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `failed_jobs`
--
ALTER TABLE `failed_jobs`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `failed_jobs_uuid_unique` (`uuid`);

--
-- Indexes for table `gst_rate`
--
ALTER TABLE `gst_rate`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `integrations`
--
ALTER TABLE `integrations`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `integrations_code_unique` (`code`);

--
-- Indexes for table `invoices`
--
ALTER TABLE `invoices`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `invoices_invoice_number_unique` (`invoice_number`),
  ADD KEY `invoices_tenant_id_index` (`tenant_id`),
  ADD KEY `invoices_subscription_id_index` (`subscription_id`),
  ADD KEY `invoices_gateway_index` (`gateway`),
  ADD KEY `invoices_status_index` (`status`);

--
-- Indexes for table `invoice_items`
--
ALTER TABLE `invoice_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `invoice_items_invoice_id_index` (`invoice_id`),
  ADD KEY `invoice_items_item_type_index` (`item_type`);

--
-- Indexes for table `license_models`
--
ALTER TABLE `license_models`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `license_models_code_unique` (`code`),
  ADD KEY `license_models_status_index` (`status`);

--
-- Indexes for table `migrations`
--
ALTER TABLE `migrations`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `password_reset_tokens`
--
ALTER TABLE `password_reset_tokens`
  ADD PRIMARY KEY (`email`);

--
-- Indexes for table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_gateway_payment` (`gateway`,`gateway_payment_id`),
  ADD KEY `payments_tenant_id_index` (`tenant_id`),
  ADD KEY `payments_subscription_id_index` (`subscription_id`),
  ADD KEY `payments_gateway_index` (`gateway`),
  ADD KEY `payments_gateway_payment_id_index` (`gateway_payment_id`),
  ADD KEY `payments_status_index` (`status`);

--
-- Indexes for table `payment_webhooks`
--
ALTER TABLE `payment_webhooks`
  ADD PRIMARY KEY (`id`),
  ADD KEY `payment_webhooks_tenant_id_index` (`tenant_id`),
  ADD KEY `payment_webhooks_invoice_id_index` (`invoice_id`),
  ADD KEY `payment_webhooks_gateway_index` (`gateway`),
  ADD KEY `payment_webhooks_event_type_index` (`event_type`),
  ADD KEY `payment_webhooks_status_index` (`status`);

--
-- Indexes for table `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  ADD PRIMARY KEY (`id`),
  ADD KEY `personal_access_tokens_tokenable_type_tokenable_id_index` (`tokenable_type`,`tokenable_id`),
  ADD KEY `fk_pat_user` (`user_id`),
  ADD KEY `fk_pat_tenant` (`tenant_id`);

--
-- Indexes for table `plan`
--
ALTER TABLE `plan`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `plan_code_unique` (`code`),
  ADD KEY `FKbo9bownwtr96u9085y9cwk32n` (`tenant_id`);

--
-- Indexes for table `plan_limitations`
--
ALTER TABLE `plan_limitations`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_plan_limitations` (`plan_id`),
  ADD KEY `plan_limitations_plan_id_index` (`plan_id`);

--
-- Indexes for table `plan_prices`
--
ALTER TABLE `plan_prices`
  ADD PRIMARY KEY (`id`),
  ADD KEY `plan_prices_plan_id_index` (`plan_id`),
  ADD KEY `plan_prices_is_active_index` (`is_active`);

--
-- Indexes for table `referrals`
--
ALTER TABLE `referrals`
  ADD PRIMARY KEY (`id`),
  ADD KEY `referrals_program_id_index` (`program_id`),
  ADD KEY `referrals_referral_code_id_index` (`referral_code_id`),
  ADD KEY `referrals_referrer_type_index` (`referrer_type`),
  ADD KEY `referrals_referrer_id_index` (`referrer_id`),
  ADD KEY `referrals_referred_tenant_id_index` (`referred_tenant_id`),
  ADD KEY `referrals_status_index` (`status`);

--
-- Indexes for table `referral_codes`
--
ALTER TABLE `referral_codes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `referral_codes_code_unique` (`code`),
  ADD KEY `referral_codes_program_id_index` (`program_id`),
  ADD KEY `referral_codes_owner_type_index` (`owner_type`),
  ADD KEY `referral_codes_owner_id_index` (`owner_id`),
  ADD KEY `referral_codes_status_index` (`status`);

--
-- Indexes for table `referral_programs`
--
ALTER TABLE `referral_programs`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `referral_programs_code_unique` (`code`),
  ADD KEY `referral_programs_reward_type_index` (`reward_type`),
  ADD KEY `referral_programs_status_index` (`status`);

--
-- Indexes for table `role`
--
ALTER TABLE `role`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `role_code_unique` (`code`);

--
-- Indexes for table `subscriptions`
--
ALTER TABLE `subscriptions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `subscriptions_tenant_id_index` (`tenant_id`),
  ADD KEY `subscriptions_plan_id_index` (`plan_id`),
  ADD KEY `subscriptions_plan_price_id_index` (`plan_price_id`),
  ADD KEY `subscriptions_status_index` (`status`);

--
-- Indexes for table `subscription_addons`
--
ALTER TABLE `subscription_addons`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_sub_addon` (`subscription_id`,`addon_id`),
  ADD KEY `subscription_addons_tenant_id_index` (`tenant_id`),
  ADD KEY `subscription_addons_subscription_id_index` (`subscription_id`),
  ADD KEY `subscription_addons_addon_id_index` (`addon_id`),
  ADD KEY `subscription_addons_status_index` (`status`);

--
-- Indexes for table `subscription_events`
--
ALTER TABLE `subscription_events`
  ADD PRIMARY KEY (`id`),
  ADD KEY `subscription_events_tenant_id_index` (`tenant_id`),
  ADD KEY `subscription_events_subscription_id_index` (`subscription_id`),
  ADD KEY `subscription_events_event_type_index` (`event_type`);

--
-- Indexes for table `tally_inventory_entries`
--
ALTER TABLE `tally_inventory_entries`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKovmf96d1lv0lqpvd9c3dyi4fs` (`voucher_id`);

--
-- Indexes for table `tally_ledger_entries`
--
ALTER TABLE `tally_ledger_entries`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK4a0hf7o6ae2ilc92hx7cg1ctd` (`voucher_id`);

--
-- Indexes for table `tally_license_validations`
--
ALTER TABLE `tally_license_validations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `tally_license_validations_tenant_id_index` (`tenant_id`),
  ADD KEY `tally_license_validations_tenant_company_id_index` (`tenant_company_id`),
  ADD KEY `tally_license_validations_tally_license_key_hash_index` (`tally_license_key_hash`),
  ADD KEY `tally_license_validations_status_index` (`status`);

--
-- Indexes for table `tally_masters`
--
ALTER TABLE `tally_masters`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKd0ahag0jilffb3o892ys4e5s3` (`guid`);

--
-- Indexes for table `tally_master_map`
--
ALTER TABLE `tally_master_map`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_master_map` (`tenant_company_id`,`master_type`,`cloud_code`),
  ADD KEY `tally_master_map_tenant_id_index` (`tenant_id`),
  ADD KEY `tally_master_map_tenant_company_id_index` (`tenant_company_id`),
  ADD KEY `tally_master_map_master_type_index` (`master_type`),
  ADD KEY `tally_master_map_cloud_code_index` (`cloud_code`),
  ADD KEY `tally_master_map_tally_name_index` (`tally_name`),
  ADD KEY `tally_master_map_tally_guid_index` (`tally_guid`),
  ADD KEY `tally_master_map_status_index` (`status`);

--
-- Indexes for table `tally_vouchers`
--
ALTER TABLE `tally_vouchers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKpdpaqxaax1lkjdabpq0h6ujao` (`guid`);

--
-- Indexes for table `tenants`
--
ALTER TABLE `tenants`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `tenants_email_unique` (`email`),
  ADD KEY `tenants_status_index` (`status`);

--
-- Indexes for table `tenant_activations`
--
ALTER TABLE `tenant_activations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `tenant_activations_tenant_id_index` (`tenant_id`),
  ADD KEY `tenant_activations_license_model_id_index` (`license_model_id`),
  ADD KEY `tenant_activations_vendor_batch_id_index` (`vendor_batch_id`),
  ADD KEY `tenant_activations_referral_id_index` (`referral_id`),
  ADD KEY `tenant_activations_activation_code_hash_index` (`activation_code_hash`),
  ADD KEY `tenant_activations_status_index` (`status`);

--
-- Indexes for table `tenant_db_configs`
--
ALTER TABLE `tenant_db_configs`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_tenant_db_configs` (`tenant_id`),
  ADD KEY `tenant_db_configs_tenant_id_index` (`tenant_id`);

--
-- Indexes for table `tenant_integrations`
--
ALTER TABLE `tenant_integrations`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_tenant_integration` (`tenant_id`,`integration_id`),
  ADD KEY `tenant_integrations_tenant_id_index` (`tenant_id`),
  ADD KEY `tenant_integrations_integration_id_index` (`integration_id`),
  ADD KEY `tenant_integrations_status_index` (`status`);

--
-- Indexes for table `tenant_settings`
--
ALTER TABLE `tenant_settings`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_tenant_settings` (`tenant_id`),
  ADD KEY `tenant_settings_tenant_id_index` (`tenant_id`);

--
-- Indexes for table `tenant_usage`
--
ALTER TABLE `tenant_usage`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_tenant_usage` (`tenant_id`),
  ADD KEY `tenant_usage_tenant_id_index` (`tenant_id`);

--
-- Indexes for table `tenant_user_role`
--
ALTER TABLE `tenant_user_role`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_tenant_user_role` (`tenant_id`,`role_id`,`user_id`),
  ADD KEY `tenant_user_role_tenant_id_index` (`tenant_id`),
  ADD KEY `tenant_user_role_role_id_index` (`role_id`),
  ADD KEY `tenant_user_role_user_id_index` (`user_id`),
  ADD KEY `tenant_user_role_is_active_index` (`is_active`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `users_email_unique` (`email`),
  ADD KEY `users_is_active_index` (`is_active`),
  ADD KEY `users_is_superadmin_index` (`is_superadmin`);

--
-- Indexes for table `user_addresses`
--
ALTER TABLE `user_addresses`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_user_id` (`user_id`);

--
-- Indexes for table `vendor`
--
ALTER TABLE `vendor`
  ADD PRIMARY KEY (`id`),
  ADD KEY `vendor_user_id_index` (`user_id`),
  ADD KEY `vendor_email_index` (`email`),
  ADD KEY `vendor_phone_index` (`phone`),
  ADD KEY `vendor_status_index` (`status`);

--
-- Indexes for table `vendors`
--
ALTER TABLE `vendors`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `vendor_activation_batches`
--
ALTER TABLE `vendor_activation_batches`
  ADD PRIMARY KEY (`id`),
  ADD KEY `vendor_activation_batches_vendor_id_index` (`vendor_id`),
  ADD KEY `vendor_activation_batches_license_model_id_index` (`license_model_id`),
  ADD KEY `vendor_activation_batches_status_index` (`status`),
  ADD KEY `vendor_activation_batches_issued_by_user_id_index` (`issued_by_user_id`),
  ADD KEY `FKi176crsr7ixo9qgbtxw3yu18t` (`plan_id`);

--
-- Indexes for table `vendor_discount`
--
ALTER TABLE `vendor_discount`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `vendor_payment_upload`
--
ALTER TABLE `vendor_payment_upload`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `vendor_tenants`
--
ALTER TABLE `vendor_tenants`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_vendor_tenant` (`tenant_id`,`vendor_id`),
  ADD KEY `vendor_tenants_tenant_id_index` (`tenant_id`),
  ADD KEY `vendor_tenants_vendor_id_index` (`vendor_id`);

--
-- Indexes for table `wallets`
--
ALTER TABLE `wallets`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_wallet_owner` (`owner_type`,`owner_id`,`currency`),
  ADD UNIQUE KEY `UK5vueofgppbqgwpfx7hqtvnyxs` (`owner_type`,`owner_id`),
  ADD KEY `wallets_owner_type_index` (`owner_type`),
  ADD KEY `wallets_owner_id_index` (`owner_id`);

--
-- Indexes for table `wallet_transactions`
--
ALTER TABLE `wallet_transactions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `wallet_transactions_wallet_id_index` (`wallet_id`),
  ADD KEY `wallet_transactions_txn_type_index` (`txn_type`),
  ADD KEY `wallet_transactions_reference_type_index` (`reference_type`),
  ADD KEY `wallet_transactions_reference_id_index` (`reference_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `activation_keys`
--
ALTER TABLE `activation_keys`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `addons`
--
ALTER TABLE `addons`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `audit_logs`
--
ALTER TABLE `audit_logs`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `bank_details`
--
ALTER TABLE `bank_details`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `bank_transfer_requests`
--
ALTER TABLE `bank_transfer_requests`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `ca`
--
ALTER TABLE `ca`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `ca_tenants`
--
ALTER TABLE `ca_tenants`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `companies_registry`
--
ALTER TABLE `companies_registry`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `company_access_otps`
--
ALTER TABLE `company_access_otps`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `company_config`
--
ALTER TABLE `company_config`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `config`
--
ALTER TABLE `config`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `connector_commands`
--
ALTER TABLE `connector_commands`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `connector_devices`
--
ALTER TABLE `connector_devices`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `connector_keys`
--
ALTER TABLE `connector_keys`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `connector_logs`
--
ALTER TABLE `connector_logs`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `connector_releases`
--
ALTER TABLE `connector_releases`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `coupons`
--
ALTER TABLE `coupons`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `coupon_redemptions`
--
ALTER TABLE `coupon_redemptions`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `email_otp`
--
ALTER TABLE `email_otp`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `events`
--
ALTER TABLE `events`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `failed_jobs`
--
ALTER TABLE `failed_jobs`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `gst_rate`
--
ALTER TABLE `gst_rate`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `integrations`
--
ALTER TABLE `integrations`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `invoices`
--
ALTER TABLE `invoices`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `invoice_items`
--
ALTER TABLE `invoice_items`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `license_models`
--
ALTER TABLE `license_models`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `migrations`
--
ALTER TABLE `migrations`
  MODIFY `id` int UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `payment_webhooks`
--
ALTER TABLE `payment_webhooks`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `plan`
--
ALTER TABLE `plan`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `plan_limitations`
--
ALTER TABLE `plan_limitations`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `plan_prices`
--
ALTER TABLE `plan_prices`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `referrals`
--
ALTER TABLE `referrals`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `referral_codes`
--
ALTER TABLE `referral_codes`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `referral_programs`
--
ALTER TABLE `referral_programs`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `role`
--
ALTER TABLE `role`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `subscriptions`
--
ALTER TABLE `subscriptions`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `subscription_addons`
--
ALTER TABLE `subscription_addons`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `subscription_events`
--
ALTER TABLE `subscription_events`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_inventory_entries`
--
ALTER TABLE `tally_inventory_entries`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_ledger_entries`
--
ALTER TABLE `tally_ledger_entries`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_license_validations`
--
ALTER TABLE `tally_license_validations`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_masters`
--
ALTER TABLE `tally_masters`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_master_map`
--
ALTER TABLE `tally_master_map`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tally_vouchers`
--
ALTER TABLE `tally_vouchers`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tenants`
--
ALTER TABLE `tenants`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tenant_activations`
--
ALTER TABLE `tenant_activations`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tenant_db_configs`
--
ALTER TABLE `tenant_db_configs`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tenant_integrations`
--
ALTER TABLE `tenant_integrations`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tenant_settings`
--
ALTER TABLE `tenant_settings`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tenant_usage`
--
ALTER TABLE `tenant_usage`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `tenant_user_role`
--
ALTER TABLE `tenant_user_role`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user_addresses`
--
ALTER TABLE `user_addresses`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `vendor`
--
ALTER TABLE `vendor`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `vendors`
--
ALTER TABLE `vendors`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `vendor_activation_batches`
--
ALTER TABLE `vendor_activation_batches`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `vendor_discount`
--
ALTER TABLE `vendor_discount`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `vendor_payment_upload`
--
ALTER TABLE `vendor_payment_upload`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `vendor_tenants`
--
ALTER TABLE `vendor_tenants`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `wallets`
--
ALTER TABLE `wallets`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `wallet_transactions`
--
ALTER TABLE `wallet_transactions`
  MODIFY `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

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
