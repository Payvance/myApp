/**
 * Copyright: © 2025 Payvance Innovation Pvt. Ltd.
 *
 * Organization: Payvance Innovation Pvt. Ltd.
 *
 * This is unpublished, proprietary, confidential source code of
 * Payvance Innovation Pvt. Ltd.
 *
 **/

/**
 *
 * author                version        date        change description
 * Neha Tembhe           1.0.0         07/01/2026   Bank details component created
 *
 **/

import InputField from "../../../components/common/inputfield/InputField";
import { formConfig } from "../../../config/formConfig";

const BankDetails = ({ bankData, setBankData, disabled }) => {

  /* Handled the changed function for the bank details form*/
  const handleChange = (name, value) => {
    setBankData(prev => ({ ...prev, [name]: value }));
  };

  return (
    <div className="profile-section">
      <div className="profile-section-title">Bank Details</div>

      <div className="form-row-4">
        <InputField
          label={formConfig.bankdetails.bankname.label}
          name="bankName"
          value={bankData.bankName}
          onChange={(e) => handleChange("bankName", e.target.value)}
          validationType="ALPHABETS_AND_SPACE"
          max={50}
          required
          disabled={disabled}
        />
        <InputField
          label={formConfig.bankdetails.branchname.label}
          name="branchName"
          value={bankData.branchName}
          onChange={(e) => handleChange("branchName", e.target.value)}
          validationType="ALPHABETS_AND_SPACE"
          max={50}
          required
          disabled={disabled}
        />
        <InputField
          label={formConfig.bankdetails.accountno.label}
          name="accountNumber"
          value={bankData.accountNumber}
          onChange={(e) => handleChange("accountNumber", e.target.value)}
          validationType="ACCOUNT_NUMBER"
          max={20}
          required
          disabled={disabled}
        />
        <InputField
          label={formConfig.bankdetails.ifsccode.label}
          name="ifscCode"
          value={bankData.ifscCode}
          onChange={(e) => handleChange("ifscCode", e.target.value)}
          validationType="IFSC_CODE"
          max={11}
          required
          disabled={disabled}
        />
      </div>
    </div>
  );
};

export default BankDetails;
