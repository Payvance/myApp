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
 * Neha Tembhe           1.0.0         07/01/2026   CA information component created
 *
 **/

import { useState } from "react";
import InputField from "../../../components/common/inputfield/InputField";
import OptionInputBox from "../../../components/common/optioninputbox/OptionInputBox";
import formConfig from "../../../config/formConfig";

const CAPersonalInfo = ({ caData, setCaData, disabled,validationErrors,setValidationErrors }) => {

  // const [validationErrors, setValidationErrors] = useState({});
  const handleChange = (field, value) => {
    if(field === "icaiMemberStatus"){
      if (value.length < 7) return;
    }
    // Handle OptionInputBox differently since it returns an object
    if ((field === "icaiMemberStatus" || field === "practiceType") && value && value.target) {
      setCaData((prev) => ({ ...prev, [field]: value.target.value }));
    } else {
      setCaData((prev) => ({
        ...prev,
        [field]: value,
      }));
    }
  };

  /* Separate handler for OptionInputBox to handle event object */
  const handleOptionChange = (field, value) => {
    const actualValue = value?.target?.value ? value.target.value : value;
    setCaData((prev) => ({ ...prev, [field]: actualValue }));
  };

  return (
    <div className="profile-section">
      <div className="profile-section-title">Chartered Accountant details</div>

      {/* ROW 1 */}
      <div className="form-row-4">      
        <InputField
          label={formConfig.caprofile.caRegNo.label}
          name="caRegNo"
          value={caData.caRegNo || ""}
          onChange={(e) => handleChange("caRegNo", e.target.value)}
          max={30}
          validationErrors={validationErrors || {}} 
          setValidationErrors={setValidationErrors}
          validationType="CA_REG_NO"
          required
          disabled={disabled}
        />
        <OptionInputBox
          label={formConfig.caprofile.membershipStatus.label}
          name="icaiMemberStatus"
          value={caData.icaiMemberStatus || ""}
          onChange={(value) => handleOptionChange("icaiMemberStatus", value)}
          options={[
            { label: "Active", value: "Active" },
            { label: "Inactive", value: "Inactive" },
          ]}
          required
          validationErrors={validationErrors || {}} 
          setValidationErrors={setValidationErrors}
          disabled={disabled}
        />
        <OptionInputBox
          label={formConfig.caprofile.practiceType.label}
          name="practiceType"
          value={caData.practiceType || ""}
          onChange={(value) => handleOptionChange("practiceType", value)}
          options={[
            { label: "Individual", value: "Individual" },
            { label: "Firm", value: "Firm" },
            { label: "Audit", value: "Audit" },
            { label: "Tax", value: "Tax" },
          ]}
          required
          validationErrors={validationErrors || {}} 
          setValidationErrors={setValidationErrors}
          disabled={disabled}
        />
        <InputField
          label={formConfig.caprofile.firmName.label}
          name="firmName"
          value={caData.firmName || ""}
          validationType="BANK"
          onChange={(e) => handleChange("firmName", e.target.value)}
          max={100}
          disabled={disabled}
        />
      </div>

      {/* ROW 2 */}
      <div className="form-row-4">
        <InputField
          label="ICAI Member No"
          name="icaiMemberNo"
          value={caData.icaiMemberNo || ""}
          onChange={(e) => handleChange("icaiMemberNo", e.target.value)}
          validationType="NUMBER_ONLY"
          min={5}
          max={6}
          required
          validationErrors={validationErrors || {}} 
          setValidationErrors={setValidationErrors}
          disabled={disabled}
        />
      </div> { /* End of second row*/}
    </div>
  );
};

export default CAPersonalInfo;