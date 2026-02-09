/**
 * Copyright: © 2025 Payvance Innovation Pvt. Ltd.
 *
 * Organization: Payvance Innovation Pvt. Ltd.
 *
 * This is unpublished, proprietary, confidential source code of Payvance Innovation Pvt. Ltd.
 * Payvance Innovation Pvt. Ltd. retains all title to and intellectual property rights in these materials.
 *
 **/

/**
 *
 * author                version        date        change description
 * Neha Tembhe           1.0.0         13/01/2026   UI of CA dashboard
 *
 **/
import React from 'react';
import CALayout from '../../../layouts/CALayout';
import CommonDashboard from '../../../components/dashboard/CommonDashboard';
import "../../../theme/LightTheme.css";
import "./CADashboard.css";

const CADashboard = () => {
  return (
    <CALayout>
      <CommonDashboard />
    </CALayout>
  );
};

export default CADashboard;
